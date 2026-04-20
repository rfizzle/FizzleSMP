GRADLE := ./gradlew
VERSION := $(shell awk -F= '/^mod_version/ {gsub(/ /,"",$$2); print $$2}' gradle.properties)
JAR := build/libs/fizzle-difficulty-$(VERSION).jar

.PHONY: help build clean test jar run-client run-server gen-sources refresh-deps version

help:
	@echo "Targets:"
	@echo "  build        Compile, test, and assemble the mod jar"
	@echo "  jar          Print the path to the built jar"
	@echo "  test         Run JUnit tests"
	@echo "  run-client   Launch a dev Minecraft client with the mod loaded"
	@echo "  run-server   Launch a dev Minecraft server with the mod loaded"
	@echo "  gen-sources  Generate Minecraft sources for IDE navigation"
	@echo "  refresh-deps Refresh Gradle dependencies"
	@echo "  clean        Remove build outputs"
	@echo "  version      Print the current mod version"

build:
	$(GRADLE) build

jar: build
	@echo $(JAR)

test:
	$(GRADLE) test

run-client:
	$(GRADLE) runClient

run-server:
	$(GRADLE) runServer

gen-sources:
	$(GRADLE) genSources

refresh-deps:
	$(GRADLE) --refresh-dependencies build

clean:
	$(GRADLE) clean

version:
	@echo $(VERSION)
