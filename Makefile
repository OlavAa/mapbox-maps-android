.PHONY: check
check:
	./gradlew ktlint lint;
	python scripts/license-validate.py;
	sh scripts/kdoc-validate.sh;

.PHONY: test
test:
	./gradlew test -i

.PHONY: build
build:
	./gradlew sdk-base:assembleRelease;
	./gradlew sdk:assembleRelease;
	./gradlew extension-style:assembleRelease;
	./gradlew module-telemetry:assembleRelease;
	./gradlew plugin-animation:assembleRelease;
	./gradlew plugin-annotation:assembleRelease;
	./gradlew plugin-attribution:assembleRelease;	
	./gradlew plugin-compass:assembleRelease;
	./gradlew plugin-gestures:assembleRelease;
	./gradlew plugin-locationcomponent:assembleRelease;
	./gradlew plugin-logo:assembleRelease;
	./gradlew plugin-overlay:assembleRelease;
	./gradlew plugin-scalebar:assembleRelease;

.PHONY: proguard
proguard:
	./gradlew clean;
	./gradlew app:installRelease;
	adb shell am start -n com.mapbox.maps.testapp/.examples.SimpleMapActivity;
	xdg-open app/build/outputs/mapping/release/mapping.txt;

.PHONY: fix
fix:
	./gradlew ktlintFormat

.PHONY: sdkRegistryUpload
sdkRegistryUpload:
	./gradlew mapboxSDKRegistryUpload;

.PHONY: sdkRegistryPublish
sdkRegistryPublish:
	python3 -m pip install git-pull-request;
	./gradlew mapboxSDKRegistryPublishAll;

.PHONY: clean
clean:
	./gradlew clean

.PHONY: codecoverage
codecoverage:
	./gradlew sdk:jacocoTestDebugUnitTestReport && google-chrome sdk/build/jacoco/jacocoHtml/index.html

.PHONY: dokka-html
dokka-html:
	./gradlew dokkaHtmlCollector

.PHONY: dokka-javadoc
dokka-javadoc:
	./gradlew dokkaJavadocCollector

# Use `make update-android-docs TAG=YourReleaseTag` while running locally.
.PHONY: update-android-docs
update-android-docs:
	sh scripts/update-android-docs.sh -s $(TAG)

.PHONY: prepare-release-doc
prepare-release-doc: dokka-html
	mkdir -p release-docs;
	cd build/dokka && zip -r dokka-docs.zip htmlCollector && cd -;
	rm -r app/build;
	zip -r examples.zip app;
	cp examples.zip release-docs/;
	cp build/dokka/dokka-docs.zip release-docs/;
	zip -r release-docs.zip release-docs;

.PHONY: unit-tests
unit-tests:
	./gradlew test -i

.PHONY: instrumentation-tests
instrumentation-tests:
	./gradlew sdk:connectedDebugAndroidTest && ./gradlew app:connectedDebugAndroidTest

.PHONY: instrumentation-clean
instrumentation-clean:
	adb uninstall com.mapbox.maps.testapp || true;
	adb uninstall com.mapbox.maps.testapp.test || true;

# Generates Activity sanity tests
.PHONY: generate-sanity-test
generate-sanity-test:
	node scripts/sanity-test/generate-sanity-test.js

# Metalava: check API
.PHONY: check-api
check-api:
	./gradlew checkApi

# Metalava: update API
.PHONY: update-api
update-api:
	./gradlew updateApi

# Metalava: update metalava version
.PHONY: update-metalava
update-metalava:
	sh ./metalava/update.sh
