import org.zipper.build.plugin.Libs

plugins {
    id("module")
}

dependencies {
    implementation(Libs.PowerJob.Worker)
    annotationProcessor(Libs.PowerJob.OfficialProcessor)
}
