package com.fizzlesmp.fizzle_enchanting.mixin;

import com.fizzlesmp.fizzle_enchanting.anvil.AnvilDispatcher;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ResultContainer;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * T-4.1.1 — structural verification for {@code AnvilMenuMixin}. Mirrors
 * {@link EnchantmentTableBlockMixinTest}: fabric-loader-junit is not on the test classpath, so the
 * Mixin transformer does not run at test time. We inspect annotations + bytecode via ASM to pin
 * down the behaviors the runtime transformer would otherwise catch —
 *
 * <ul>
 *   <li>Drift in {@code AnvilMenu#createResult}'s signature.
 *   <li>A missing/widened {@code @Mixin} target.
 *   <li>The {@code @Inject} missing the TAIL target.
 *   <li>The injector body failing to (a) call {@link AnvilDispatcher#handle} or (b) forward the
 *       result into {@code resultSlots.setItem}/{@code cost.set} — i.e. the "stub dispatcher
 *       returning a canned result → anvil output slot receives it" contract.
 *   <li>The mixin config forgetting to list the mixin.
 * </ul>
 */
class AnvilMenuMixinTest {

    private static final String MIXIN_DESC = Type.getDescriptor(org.spongepowered.asm.mixin.Mixin.class);
    private static final String INJECT_DESC =
            Type.getDescriptor(org.spongepowered.asm.mixin.injection.Inject.class);
    private static final String AT_DESC = Type.getDescriptor(org.spongepowered.asm.mixin.injection.At.class);
    private static final String TARGET_INTERNAL_NAME = Type.getInternalName(AnvilMenu.class);

    @Test
    void targetMethod_stillExistsOnAnvilMenu() throws NoSuchMethodException {
        Method m = AnvilMenu.class.getDeclaredMethod("createResult");
        assertEquals(void.class, m.getReturnType(),
                "mixin assumes createResult() is void — if Mojang retypes it, fix the mixin and this test together");
    }

    @Test
    void mixinClass_annotatesAnvilMenu() throws Exception {
        ClassNode node = readMixinClass();
        AnnotationNode mixinAnn = findAnnotation(node.visibleAnnotations, MIXIN_DESC);
        if (mixinAnn == null) {
            mixinAnn = findAnnotation(node.invisibleAnnotations, MIXIN_DESC);
        }
        assertNotNull(mixinAnn, "@Mixin annotation must be present for the transformer to pick this up");

        List<?> values = extractArrayValue(mixinAnn, "value");
        assertNotNull(values, "@Mixin must specify value = AnvilMenu.class");
        assertEquals(1, values.size(), "a single target — the mixin should not widen beyond AnvilMenu");
        assertEquals(TARGET_INTERNAL_NAME, ((Type) values.get(0)).getInternalName(),
                "@Mixin target must be AnvilMenu — a rename here silently disables the dispatcher hook");
    }

    @Test
    void injectMethod_hasTailOnCreateResult() throws Exception {
        ClassNode node = readMixinClass();
        MethodNode hook = findInjectHook(node);
        assertNotNull(hook, "mixin must contain exactly one @Inject hook");

        AnnotationNode inject = findAnnotation(hook.invisibleAnnotations, INJECT_DESC);
        if (inject == null) inject = findAnnotation(hook.visibleAnnotations, INJECT_DESC);
        assertNotNull(inject);

        List<String> methods = extractArrayValue(inject, "method");
        assertNotNull(methods, "@Inject must declare method = \"createResult\"");
        assertEquals(List.of("createResult"), methods);

        List<AnnotationNode> ats = extractArrayValue(inject, "at");
        assertNotNull(ats, "@Inject must declare at = @At(...)");
        assertEquals(1, ats.size());
        assertEquals(AT_DESC, ats.get(0).desc);
        assertEquals("TAIL", extractValue(ats.get(0), "value"),
                "@At TAIL — we want to overwrite vanilla's result, not replace the whole method");

        // Void createResult() → hook descriptor must be (Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;)V.
        String expected = "(" + Type.getDescriptor(CallbackInfo.class) + ")V";
        assertEquals(expected, hook.desc,
                "hook params must mirror createResult + CallbackInfo for the injector to bind");
    }

    @Test
    void injectorBody_callsDispatcherAndForwardsResult() throws Exception {
        ClassNode node = readMixinClass();
        MethodNode hook = findInjectHook(node);
        assertNotNull(hook);

        boolean callsDispatcher = false;
        boolean setsResultSlot = false;
        boolean setsCost = false;
        boolean setsRepairCount = false;

        String dispatcherInternal = Type.getInternalName(AnvilDispatcher.class);
        String resultContainerInternal = Type.getInternalName(ResultContainer.class);
        String dataSlotInternal = Type.getInternalName(DataSlot.class);

        for (AbstractInsnNode insn = hook.instructions.getFirst(); insn != null; insn = insn.getNext()) {
            if (!(insn instanceof MethodInsnNode call)) continue;

            if (insn.getOpcode() == Opcodes.INVOKESTATIC
                    && dispatcherInternal.equals(call.owner)
                    && "handle".equals(call.name)) {
                callsDispatcher = true;
            }
            // ItemCombinerMenu declares resultSlots as ResultContainer, so javac emits
            // INVOKEVIRTUAL against that concrete type (not the Container interface).
            if (insn.getOpcode() == Opcodes.INVOKEVIRTUAL
                    && resultContainerInternal.equals(call.owner)
                    && "setItem".equals(call.name)) {
                setsResultSlot = true;
            }
            if (insn.getOpcode() == Opcodes.INVOKEVIRTUAL
                    && dataSlotInternal.equals(call.owner)
                    && "set".equals(call.name)) {
                setsCost = true;
            }
            if (insn.getOpcode() == Opcodes.INVOKEINTERFACE
                    && "fizzleEnchanting$setRepairItemCountCost".equals(call.name)) {
                setsRepairCount = true;
            }
        }

        assertTrue(callsDispatcher,
                "hook must call AnvilDispatcher.handle — this is how handlers get a shot at the slot pair");
        assertTrue(setsResultSlot,
                "hook must call Container.setItem on resultSlots when a handler claims the pair — "
                        + "the canned-result contract from T-4.1.1");
        assertTrue(setsCost,
                "hook must call DataSlot.set to overwrite the cost when a handler fires");
        assertTrue(setsRepairCount,
                "hook must call the accessor's repairItemCountCost setter to reflect material consumption");
    }

    @Test
    void mixinConfig_listsThisMixin() throws Exception {
        try (InputStream in = getClass().getClassLoader()
                .getResourceAsStream("fizzle_enchanting.mixins.json")) {
            assertNotNull(in, "fizzle_enchanting.mixins.json must be on the test classpath");
            JsonObject root = JsonParser.parseReader(
                    new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
            List<String> mixins = new ArrayList<>();
            root.getAsJsonArray("mixins").forEach(e -> mixins.add(e.getAsString()));
            assertTrue(mixins.contains("AnvilMenuMixin"),
                    "mixin config must list AnvilMenuMixin — otherwise the transformer never sees it");
        }
    }

    // --- ASM helpers ---

    private static MethodNode findInjectHook(ClassNode node) {
        MethodNode hook = null;
        int injectCount = 0;
        for (MethodNode m : node.methods) {
            if (findAnnotation(m.invisibleAnnotations, INJECT_DESC) != null
                    || findAnnotation(m.visibleAnnotations, INJECT_DESC) != null) {
                hook = m;
                injectCount++;
            }
        }
        assertEquals(1, injectCount, "exactly one @Inject hook in MVP — more would widen the mixin footprint");
        return hook;
    }

    private static ClassNode readMixinClass() throws Exception {
        String path = AnvilMenuMixin.class.getName().replace('.', '/') + ".class";
        try (InputStream in = AnvilMenuMixin.class.getClassLoader().getResourceAsStream(path)) {
            assertNotNull(in, "mixin class file must be loadable from the classpath");
            ClassReader reader = new ClassReader(in);
            ClassNode node = new ClassNode();
            reader.accept(node, 0);
            return node;
        }
    }

    private static AnnotationNode findAnnotation(List<AnnotationNode> annotations, String descriptor) {
        if (annotations == null) return null;
        for (AnnotationNode a : annotations) {
            if (descriptor.equals(a.desc)) return a;
        }
        return null;
    }

    private static Object extractValue(AnnotationNode annotation, String key) {
        if (annotation.values == null) return null;
        for (int i = 0; i + 1 < annotation.values.size(); i += 2) {
            if (key.equals(annotation.values.get(i))) {
                return annotation.values.get(i + 1);
            }
        }
        return null;
    }

    @SuppressWarnings({"unchecked", "unused"})
    private static <T> List<T> extractArrayValue(AnnotationNode annotation, String key) {
        Object v = extractValue(annotation, key);
        return v == null ? null : (List<T>) v;
    }
}
