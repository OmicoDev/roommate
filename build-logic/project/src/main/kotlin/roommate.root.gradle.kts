plugins {
    id("me.omico.consensus.root")
    id("roommate.gradm")
    id("roommate.root.git")
    id("roommate.root.spotless")
}

tasks.named<Wrapper>("wrapper") {
    gradleVersion = versions.gradle
    distributionType = Wrapper.DistributionType.BIN
}
