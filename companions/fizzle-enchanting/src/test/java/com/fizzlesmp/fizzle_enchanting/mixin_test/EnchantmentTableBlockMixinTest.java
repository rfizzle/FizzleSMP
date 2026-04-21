package com.fizzlesmp.fizzle_enchanting.mixin_test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EnchantingTableBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
 * T-2.5.2 — structural verification that {@code EnchantmentTableBlockMixin}
 * correctly targets {@code EnchantingTableBlock#getMenuProvider}.
 *
 * <p>fabric-loader-junit is not on the test classpath (matches fizzle-difficulty),
 * so the mixin transformer does not run during unit tests. The mixin-related
 * annotations ({@link org.spongepowered.asm.mixin.Mixin @Mixin},
 * {@link org.spongepowered.asm.mixin.injection.Inject @Inject}) have CLASS
 * retention, so we inspect them through ASM to defend against the failure
 * modes the runtime transformer would catch — drift in the target method's
 * signature, a missing annotation, or a mixin the config forgot to register.
 */
class EnchantmentTableBlockMixinTest {

    private static final String MIXIN_DESC = Type.getDescriptor(org.spongepowered.asm.mixin.Mixin.class);
    private static final String INJECT_DESC =
            Type.getDescriptor(org.spongepowered.asm.mixin.injection.Inject.class);
    private static final String AT_DESC = Type.getDescriptor(org.spongepowered.asm.mixin.injection.At.class);
    private static final String TARGET_INTERNAL_NAME = Type.getInternalName(EnchantingTableBlock.class);

    @Test
    void targetMethod_stillExistsOnEnchantingTableBlock() throws NoSuchMethodException {
        Method m = EnchantingTableBlock.class.getDeclaredMethod(
                "getMenuProvider", BlockState.class, Level.class, BlockPos.class);
        assertEquals(MenuProvider.class, m.getReturnType(),
                "mixin assumes getMenuProvider still returns MenuProvider — "
                        + "if Mojang renames/retypes it, fix the mixin and this test together");
    }

    @Test
    void mixinClass_annotatesEnchantingTableBlock() throws Exception {
        ClassNode node = readMixinClass();
        AnnotationNode mixinAnn = findAnnotation(node.visibleAnnotations, MIXIN_DESC);
        if (mixinAnn == null) {
            mixinAnn = findAnnotation(node.invisibleAnnotations, MIXIN_DESC);
        }
        assertNotNull(mixinAnn, "@Mixin annotation must be present for the transformer to pick this up");

        List<?> values = extractArrayValue(mixinAnn, "value");
        assertNotNull(values, "@Mixin must specify value = EnchantingTableBlock.class");
        assertEquals(1, values.size(), "a single target — the mixin should not widen beyond EnchantingTableBlock");
        assertEquals(TARGET_INTERNAL_NAME, ((Type) values.get(0)).getInternalName(),
                "@Mixin target must be EnchantingTableBlock — a rename here silently disables the menu swap");
    }

    @Test
    void injectMethod_hasHeadCancellableOnGetMenuProvider() throws Exception {
        ClassNode node = readMixinClass();
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
        assertNotNull(hook);

        AnnotationNode inject = findAnnotation(hook.invisibleAnnotations, INJECT_DESC);
        if (inject == null) inject = findAnnotation(hook.visibleAnnotations, INJECT_DESC);
        assertNotNull(inject);

        List<String> methods = extractArrayValue(inject, "method");
        assertNotNull(methods, "@Inject must declare method = \"getMenuProvider\"");
        assertEquals(List.of("getMenuProvider"), methods);

        Object cancellable = extractValue(inject, "cancellable");
        assertEquals(Boolean.TRUE, cancellable,
                "hook must be cancellable so setReturnValue actually short-circuits vanilla");

        List<AnnotationNode> ats = extractArrayValue(inject, "at");
        assertNotNull(ats, "@Inject must declare at = @At(...)");
        assertEquals(1, ats.size());
        assertEquals(AT_DESC, ats.get(0).desc);
        assertEquals("HEAD", extractValue(ats.get(0), "value"),
                "@At HEAD — we want to pre-empt vanilla's BE lookup, not piggyback on it");

        // Verify the hook's descriptor matches vanilla getMenuProvider + the CIR tail.
        String expected = "("
                + Type.getDescriptor(BlockState.class)
                + Type.getDescriptor(Level.class)
                + Type.getDescriptor(BlockPos.class)
                + Type.getDescriptor(CallbackInfoReturnable.class)
                + ")V";
        assertEquals(expected, hook.desc,
                "hook params must mirror getMenuProvider + CallbackInfoReturnable for the injector to bind");
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
            assertTrue(mixins.contains("EnchantmentTableBlockMixin"),
                    "mixin config must list EnchantmentTableBlockMixin — otherwise the transformer never sees it");
        }
    }

    // --- ASM helpers ---

    private static ClassNode readMixinClass() throws Exception {
        // String path — a class literal on a mixin-package class trips Knot's IllegalClassLoadError.
        String path = "com/fizzlesmp/fizzle_enchanting/mixin/EnchantmentTableBlockMixin.class";
        try (InputStream in = EnchantmentTableBlockMixinTest.class.getClassLoader().getResourceAsStream(path)) {
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

    @SuppressWarnings("unchecked")
    private static <T> List<T> extractArrayValue(AnnotationNode annotation, String key) {
        Object v = extractValue(annotation, key);
        return v == null ? null : (List<T>) v;
    }
}
