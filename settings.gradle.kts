rootProject.name = "GameEngine"
include("src:main:test")
findProject(":src:main:test")?.name = "test"
