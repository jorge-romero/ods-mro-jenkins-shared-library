package org.ods.util

import java.nio.file.Files
import java.nio.file.Paths

import spock.lang.*

import static util.FixtureHelper.*

import util.*

class MROPipelineUtilSpec extends SpecHelper {

    def "get build environment for DEBUG"() {
        given:
        def steps = Spy(util.PipelineSteps)
        def util = new MROPipelineUtil(steps, Mock(GitUtil))

        when:
        def result = util.getBuildEnvironment()

        then:
        result.find { it == "DEBUG=false" }

        when:
        result = util.getBuildEnvironment(true)

        then:
        result.find { it == "DEBUG=true" }

        when:
        result = util.getBuildEnvironment(false)

        then:
        result.find { it == "DEBUG=false" }
    }

    def "get build environment for MULTI_REPO_BUILD"() {
        given:
        def steps = Spy(util.PipelineSteps)
        def util = new MROPipelineUtil(steps, Mock(GitUtil))

        when:
        def result = util.getBuildEnvironment()

        then:
        result.find { it == "MULTI_REPO_BUILD=true" }
    }

    def "get build environment for MULTI_REPO_ENV"() {
        given:
        def steps = Spy(util.PipelineSteps)
        def util = new MROPipelineUtil(steps, Mock(GitUtil))

        when:
        steps.env.environment = null
        def result = util.getBuildEnvironment()

        then:
        result.find { it == "MULTI_REPO_ENV=dev" }

        when:
        steps.env.environment = ""
        result = util.getBuildEnvironment()

        then:
        result.find { it == "MULTI_REPO_ENV=dev" }

        when:
        steps.env.environment = "myEnv"
        result = util.getBuildEnvironment()

        then:
        result.find { it == "MULTI_REPO_ENV=myEnv" }
    }

    def "get build environment for MULTI_REPO_ENV_TOKEN"() {
        given:
        def steps = Spy(util.PipelineSteps)
        def util = new MROPipelineUtil(steps, Mock(GitUtil))

        when:
        steps.env.environment = "dev"
        def result = util.getBuildEnvironment()

        then:
        result.find { it == "MULTI_REPO_ENV_TOKEN=D" }

        when:
        steps.env.environment = "qa"
        result = util.getBuildEnvironment()

        then:
        result.find { it == "MULTI_REPO_ENV_TOKEN=Q" }

        when:
        steps.env.environment = "prod"
        result = util.getBuildEnvironment()

        then:
        result.find { it == "MULTI_REPO_ENV_TOKEN=P" }
    }

    def "get build environment for RELEASE_PARAM_CHANGE_ID"() {
        given:
        def steps = Spy(util.PipelineSteps)
        def util = new MROPipelineUtil(steps, Mock(GitUtil))

        when:
        steps.env.changeId = null
        steps.env.environment = "myEnv"
        steps.env.version = "0.1"
        def result = util.getBuildEnvironment()

        then:
        result.find { it == "RELEASE_PARAM_CHANGE_ID=0.1-myEnv" }

        when:
        steps.env.changeId = ""
        steps.env.environment = "myEnv"
        steps.env.version = "0.1"
        result = util.getBuildEnvironment()

        then:
        result.find { it == "RELEASE_PARAM_CHANGE_ID=0.1-myEnv" }

        when:
        steps.env.changeId = "myId"
        steps.env.environment = "myEnv"
        steps.env.version = "0.1"
        result = util.getBuildEnvironment()

        then:
        result.find { it == "RELEASE_PARAM_CHANGE_ID=myId" }
    }

    def "get build environment for RELEASE_PARAM_CHANGE_DESC"() {
        given:
        def steps = Spy(util.PipelineSteps)
        def util = new MROPipelineUtil(steps, Mock(GitUtil))

        when:
        steps.env.changeDescription = null
        def result = util.getBuildEnvironment()

        then:
        result.find { it == "RELEASE_PARAM_CHANGE_DESC=UNDEFINED" }

        when:
        steps.env.changeDescription = ""
        result = util.getBuildEnvironment()

        then:
        result.find { it == "RELEASE_PARAM_CHANGE_DESC=UNDEFINED" }

        when:
        steps.env.changeDescription = "myDescription"
        result = util.getBuildEnvironment()

        then:
        result.find { it == "RELEASE_PARAM_CHANGE_DESC=myDescription" }
    }

    def "get build environment for RELEASE_PARAM_CONFIG_ITEM"() {
        given:
        def steps = Spy(util.PipelineSteps)
        def util = new MROPipelineUtil(steps, Mock(GitUtil))

        when:
        steps.env.configItem = null
        def result = util.getBuildEnvironment()

        then:
        result.find { it == "RELEASE_PARAM_CONFIG_ITEM=UNDEFINED" }

        when:
        steps.env.configItem = ""
        result = util.getBuildEnvironment()

        then:
        result.find { it == "RELEASE_PARAM_CONFIG_ITEM=UNDEFINED" }

        when:
        steps.env.configItem = "myItem"
        result = util.getBuildEnvironment()

        then:
        result.find { it == "RELEASE_PARAM_CONFIG_ITEM=myItem" }
    }

    def "get build environment for RELEASE_PARAM_VERSION"() {
        given:
        def steps = Spy(util.PipelineSteps)
        def util = new MROPipelineUtil(steps, Mock(GitUtil))

        when:
        steps.env.version = null
        def result = util.getBuildEnvironment()

        then:
        result.find { it == "RELEASE_PARAM_VERSION=WIP" }

        when:
        steps.env.version = ""
        result = util.getBuildEnvironment()

        then:
        result.find { it == "RELEASE_PARAM_VERSION=WIP" }

        when:
        steps.env.version = "0.1"
        result = util.getBuildEnvironment()

        then:
        result.find { it == "RELEASE_PARAM_VERSION=0.1" }
    }

    def "get build environment for SOURCE_CLONE_ENV"() {
        given:
        def steps = Spy(util.PipelineSteps)
        def util = new MROPipelineUtil(steps, Mock(GitUtil))

        when:
        steps.env.environment = "myEnv"
        steps.env.sourceEnvironmentToClone = null
        def result = util.getBuildEnvironment()

        then:
        result.find { it == "SOURCE_CLONE_ENV=myEnv" }

        when:
        steps.env.environment = "myEnv"
        steps.env.sourceEnvironmentToClone = ""
        result = util.getBuildEnvironment()

        then:
        result.find { it == "SOURCE_CLONE_ENV=myEnv" }

        when:
        steps.env.environment = "mvEnv"
        steps.env.sourceEnvironmentToClone = "mySourceEnv"
        result = util.getBuildEnvironment()

        then:
        result.find { it == "SOURCE_CLONE_ENV=mySourceEnv" }
    }

    def "get build environment for SOURCE_CLONE_ENV_TOKEN"() {
        given:
        def steps = Spy(util.PipelineSteps)
        def util = new MROPipelineUtil(steps, Mock(GitUtil))

        when:
        steps.env.sourceEnvironmentToClone = "dev"
        def result = util.getBuildEnvironment()

        then:
        result.find { it == "SOURCE_CLONE_ENV_TOKEN=D" }

        when:
        steps.env.sourceEnvironmentToClone = "qa"
        result = util.getBuildEnvironment()

        then:
        result.find { it == "SOURCE_CLONE_ENV_TOKEN=Q" }

        when:
        steps.env.sourceEnvironmentToClone = "prod"
        result = util.getBuildEnvironment()

        then:
        result.find { it == "SOURCE_CLONE_ENV_TOKEN=P" }
    }

    def "get build param for changeDescription"() {
        given:
        def steps = Spy(util.PipelineSteps)
        def util = new MROPipelineUtil(steps, Mock(GitUtil))

        when:
        steps.env.changeDescription = null
        def result = util.getBuildParams()

        then:
        result.changeDescription == "UNDEFINED"

        when:
        steps.env.changeDescription = ""
        result = util.getBuildParams()

        then:
        result.changeDescription == "UNDEFINED"

        when:
        steps.env.changeDescription = "myDescription"
        result = util.getBuildParams()

        then:
        result.changeDescription == "myDescription"
    }

    def "get build param for changeId"() {
        given:
        def steps = Spy(util.PipelineSteps)
        def util = new MROPipelineUtil(steps, Mock(GitUtil))

        when:
        steps.env.changeId = null
        steps.env.environment = "myEnv"
        steps.env.version = "0.1"
        def result = util.getBuildParams()

        then:
        result.changeId == "0.1-myEnv"

        when:
        steps.env.changeId = ""
        steps.env.environment = "myEnv"
        steps.env.version = "0.1"
        result = util.getBuildParams()

        then:
        result.changeId == "0.1-myEnv"

        when:
        steps.env.changeId = "myId"
        steps.env.environment = "myEnv"
        steps.env.version = "0.1"
        result = util.getBuildParams()

        then:
        result.changeId == "myId"
    }

    def "get build param for configItem"() {
        given:
        def steps = Spy(util.PipelineSteps)
        def util = new MROPipelineUtil(steps, Mock(GitUtil))

        when:
        steps.env.configItem = null
        def result = util.getBuildParams()

        then:
        result.configItem == "UNDEFINED"

        when:
        steps.env.configItem = ""
        result = util.getBuildParams()

        then:
        result.configItem == "UNDEFINED"

        when:
        steps.env.configItem = "myItem"
        result = util.getBuildParams()

        then:
        result.configItem == "myItem"
    }

    def "get build param for sourceEnvironmentToClone"() {
        given:
        def steps = Spy(util.PipelineSteps)
        def util = new MROPipelineUtil(steps, Mock(GitUtil))

        when:
        steps.env.environment = "myEnv"
        steps.env.sourceEnvironmentToClone = null
        def result = util.getBuildParams()

        then:
        result.sourceEnvironmentToClone == "myEnv"

        when:
        steps.env.environment = "myEnv"
        steps.env.sourceEnvironmentToClone = ""
        result = util.getBuildParams()

        then:
        result.sourceEnvironmentToClone == "myEnv"

        when:
        steps.env.environment = "mvEnv"
        steps.env.sourceEnvironmentToClone = "mySourceEnv"
        result = util.getBuildParams()

        then:
        result.sourceEnvironmentToClone == "mySourceEnv"
    }

    def "get build param for sourceEnvironmentToCloneToken"() {
        given:
        def steps = Spy(util.PipelineSteps)
        def util = new MROPipelineUtil(steps, Mock(GitUtil))

        when:
        steps.env.sourceEnvironmentToClone = "dev"
        def result = util.getBuildParams()

        then:
        result.sourceEnvironmentToCloneToken == "D"

        when:
        steps.env.sourceEnvironmentToClone = "qa"
        result = util.getBuildParams()

        then:
        result.sourceEnvironmentToCloneToken == "Q"

        when:
        steps.env.sourceEnvironmentToClone = "prod"
        result = util.getBuildParams()

        then:
        result.sourceEnvironmentToCloneToken == "P"
    }

    def "get build param for targetEnvironment"() {
        given:
        def steps = Spy(util.PipelineSteps)
        def util = new MROPipelineUtil(steps, Mock(GitUtil))

        when:
        steps.env.environment = null
        def result = util.getBuildParams()

        then:
        result.targetEnvironment == "dev"

        when:
        steps.env.environment = ""
        result = util.getBuildParams()

        then:
        result.targetEnvironment == "dev"

        when:
        steps.env.environment = "myEnv"
        result = util.getBuildParams()

        then:
        result.targetEnvironment == "myEnv"
    }

    def "get build param for targetEnvironmentToken"() {
        given:
        def steps = Spy(util.PipelineSteps)
        def util = new MROPipelineUtil(steps, Mock(GitUtil))

        when:
        steps.env.environment = "dev"
        def result = util.getBuildParams()

        then:
        result.targetEnvironmentToken == "D"

        when:
        steps.env.environment = "qa"
        result = util.getBuildParams()

        then:
        result.targetEnvironmentToken == "Q"

        when:
        steps.env.environment = "prod"
        result = util.getBuildParams()

        then:
        result.targetEnvironmentToken == "P"
    }

    def "get build param for version"() {
        given:
        def steps = Spy(util.PipelineSteps)
        def util = new MROPipelineUtil(steps, Mock(GitUtil))

        when:
        steps.env.version = null
        def result = util.getBuildParams()

        then:
        result.version == "WIP"

        when:
        steps.env.version = ""
        result = util.getBuildParams()

        then:
        result.version == "WIP"

        when:
        steps.env.version = "0.1"
        result = util.getBuildParams()

        then:
        result.version == "0.1"
    }

    def "is triggered by change management process"() {
        given:
        def steps = Spy(util.PipelineSteps)
        def util = new MROPipelineUtil(steps, Mock(GitUtil))

        when:
        steps.env.changeId = "0815"
        steps.env.configItem = "myItem"
        def result = util.isTriggeredByChangeManagementProcess()

        then:
        result

        when:
        steps.env.changeId = "0815"
        steps.env.configItem = null
        result = util.isTriggeredByChangeManagementProcess()

        then:
        !result

        when:
        steps.env.changeId = null
        steps.env.configItem = "myItem"
        result = util.isTriggeredByChangeManagementProcess()

        then:
        !result

        when:
        steps.env.changeId = null
        steps.env.configItem = null
        result = util.isTriggeredByChangeManagementProcess()

        then:
        !result
    }

    def "load a repo's pipeline config"() {
        given:
        def steps = Spy(util.PipelineSteps)
        def util = new MROPipelineUtil(steps, Mock(GitUtil))

        def repoPath = Paths.get(steps.env.WORKSPACE, MROPipelineUtil.REPOS_BASE_DIR, "A").toString()
        def repoDir = util.createDirectory(repoPath)
        def repos = createProject().repositories

        def componentMetadataFile = Paths.get(repoPath, MROPipelineUtil.COMPONENT_METADATA_FILE_NAME)
        def pipelineConfigFile = Paths.get(repoPath, MROPipelineUtil.PipelineConfig.FILE_NAMES.first())

        when:
        componentMetadataFile << """
        id: myId
        name: myName
        description: myDescription
        supplier: mySupplier
        version: myVersion
        references: myReferences
        """

        pipelineConfigFile << """
        dependencies:
          - B

        phases:
          build:
            type: Makefile
            target: build
          test:
            type: ShellScript
            script: test.sh
        """

        def result = util.loadPipelineConfig(repoPath, repos[0].clone())

        then:
        def expected = repos[0] << [
            metadata: [
                id: "myId",
                name: "myName",
                description: "myDescription",
                supplier: "mySupplier",
                version: "myVersion",
                references: "myReferences"
            ],
            pipelineConfig: [
                dependencies: ["B"],

                phases: [
                    build: [
                        type: "Makefile",
                        target: "build"
                    ],
                    test: [
                        type: "ShellScript",
                        script: "test.sh"
                    ]
                ]
            ]
        ]

        result == expected

        cleanup:
        repoDir.deleteDir()
    }

    def "load a repo's pipeline config with invalid path"() {
        given:
        def steps = Spy(util.PipelineSteps)
        def util = new MROPipelineUtil(steps, Mock(GitUtil))

        def repos = createProject().repositories

        when:
        util.loadPipelineConfig(null, repos[0])

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Error: unable to parse pipeline config. 'path' is undefined."

        when:
        util.loadPipelineConfig("", repos[0])

        then:
        e = thrown(IllegalArgumentException)
        e.message == "Error: unable to parse pipeline config. 'path' is undefined."

        when:
        def path = "myPath"
        util.loadPipelineConfig(path, repos[0])

        then:
        e = thrown(IllegalArgumentException)
        e.message == "Error: unable to parse pipeline config. 'path' must be inside the Jenkins workspace: ${path}"
    }

    def "load a repo's pipeline config with invalid repo"() {
        given:
        def steps = Spy(util.PipelineSteps)
        def util = new MROPipelineUtil(steps, Mock(GitUtil))

        def repoPath = Paths.get(steps.env.WORKSPACE, MROPipelineUtil.REPOS_BASE_DIR, "A").toString()
        def repoDir = util.createDirectory(repoPath)
        def repos = createProject().repositories

        def componentMetadataFile = Paths.get(repoPath, MROPipelineUtil.PipelineConfig.COMPONENT_METADATA_FILE_NAME)
        def pipelineConfigFile = Paths.get(repoPath, MROPipelineUtil.PipelineConfig.FILE_NAMES.first())

        when:
        pipelineConfigFile << """
        phases:
          build:
        """

        util.loadPipelineConfig(repoPath, repos[0])

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Error: unable to parse pipeline phase config. Required attribute 'phase.type' is undefined in phase 'build'."

        when:
        pipelineConfigFile.text = """
        phases:
          build:
            type: someType
        """

        util.loadPipelineConfig(repoPath, repos[0])

        then:
        e = thrown(IllegalArgumentException)
        e.message == "Error: unable to parse pipeline phase config. Attribute 'phase.type' contains an unsupported value 'someType' in phase 'build'. Supported types are: ${MROPipelineUtil.PipelineConfig.PHASE_EXECUTOR_TYPES}."

        when:
        pipelineConfigFile.text = """
        dependencies: []
        """

        componentMetadataFile << """
        id: myId
        """

        util.loadPipelineConfig(repoPath, repos[0])

        then:
        e = thrown(IllegalArgumentException)
        e.message == "Error: unable to parse component metadata. Required attribute 'name' is undefined for repository '${repos.first().id}'."

        when:
        componentMetadataFile.text = """
        id: myId
        name: myName
        """

        util.loadPipelineConfig(repoPath, repos[0])

        then:
        e = thrown(IllegalArgumentException)
        e.message == "Error: unable to parse component metadata. Required attribute 'description' is undefined for repository '${repos.first().id}'."

        when:
        componentMetadataFile.text = """
        id: myId
        name: myName
        description: myDescription
        """

        util.loadPipelineConfig(repoPath, repos[0])

        then:
        e = thrown(IllegalArgumentException)
        e.message == "Error: unable to parse component metadata. Required attribute 'supplier' is undefined for repository '${repos.first().id}'."

        when:
        componentMetadataFile.text = """
        id: myId
        name: myName
        description: myDescription
        supplier: mySupplier
        """

        util.loadPipelineConfig(repoPath, repos[0])

        then:
        e = thrown(IllegalArgumentException)
        e.message == "Error: unable to parse component metadata. Required attribute 'version' is undefined for repository '${repos.first().id}'."

        cleanup:
        repoDir.deleteDir()
    }

    def "load a repo's pipeline config with invalid phase type"() {
        given:
        def steps = Spy(util.PipelineSteps)
        def util = new MROPipelineUtil(steps, Mock(GitUtil))

        when:
        util.loadPipelineConfig(steps.env.WORKSPACE, null)

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Error: unable to parse pipeline config. 'repo' is undefined."

        when:
        util.loadPipelineConfig(steps.env.WORKSPACE, [:])

        then:
        e = thrown(IllegalArgumentException)
        e.message == "Error: unable to parse pipeline config. 'repo' is undefined."
    }

    def "load a repo's pipeline config with invalid target for phase type Makefile"() {
        given:
        def steps = Spy(util.PipelineSteps)
        def util = new MROPipelineUtil(steps, Mock(GitUtil))

        def repoPath = Paths.get(steps.env.WORKSPACE, MROPipelineUtil.REPOS_BASE_DIR, "A").toString()
        def repoDir = util.createDirectory(repoPath)
        def repos = createProject().repositories

        def componentMetadataFile = Paths.get(repoPath, MROPipelineUtil.COMPONENT_METADATA_FILE_NAME)
        def pipelineConfigFile = Paths.get(repoPath, MROPipelineUtil.PipelineConfig.FILE_NAMES.first())

        when:
        componentMetadataFile << """
        id: myId
        name: myName
        description: myDescription
        supplier: mySupplier
        version: myVersion
        references: myReferences
        """

        pipelineConfigFile << """
        phases:
          build:
            type: Makefile
        """

        util.loadPipelineConfig(repoPath, repos[0])

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Error: unable to parse pipeline phase config. Required attribute 'phase.target' is undefined in phase 'build'."

        when:
        componentMetadataFile.text = """
        id: myId
        name: myName
        description: myDescription
        supplier: mySupplier
        version: myVersion
        references: myReferences
        """

        pipelineConfigFile.text = """
        phases:
          build:
            type: Makefile
            target: 
        """

        util.loadPipelineConfig(repoPath, repos[0])

        then:
        e = thrown(IllegalArgumentException)
        e.message == "Error: unable to parse pipeline phase config. Required attribute 'phase.target' is undefined in phase 'build'."

        cleanup:
        repoDir.deleteDir()
    }

    def "load a repo's pipeline config with invalid target for phase type ShellScript"() {
        given:
        def steps = Spy(util.PipelineSteps)
        def util = new MROPipelineUtil(steps, Mock(GitUtil))

        def repoPath = Paths.get(steps.env.WORKSPACE, MROPipelineUtil.REPOS_BASE_DIR, "A").toString()
        def repoDir = util.createDirectory(repoPath)
        def repos = createProject().repositories

        def pipelineConfigFile = Paths.get(repoPath, MROPipelineUtil.PipelineConfig.FILE_NAMES.first())
        def componentMetadataFile = Paths.get(repoPath, MROPipelineUtil.COMPONENT_METADATA_FILE_NAME)

        when:
        componentMetadataFile << """
        id: myId
        name: myName
        description: myDescription
        supplier: mySupplier
        version: myVersion
        references: myReferences
        """

        pipelineConfigFile << """
        phases:
          build:
            type: ShellScript
        """

        util.loadPipelineConfig(repoPath, repos[0])

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Error: unable to parse pipeline phase config. Required attribute 'phase.script' is undefined in phase 'build'."

        when:
        componentMetadataFile.text = """
        id: myId
        name: myName
        description: myDescription
        supplier: mySupplier
        version: myVersion
        references: myReferences
        """

        pipelineConfigFile.text = """
        phases:
          build:
            type: ShellScript
            script: 
        """

        util.loadPipelineConfig(repoPath, repos[0])

        then:
        e = thrown(IllegalArgumentException)
        e.message == "Error: unable to parse pipeline phase config. Required attribute 'phase.script' is undefined in phase 'build'."

        cleanup:
        repoDir.deleteDir()
    }

    def "load a repo's pipeline config with missing metadata.yml"() {
        given:
        def steps = Spy(util.PipelineSteps)
        def util = new MROPipelineUtil(steps, Mock(GitUtil))

        def repoPath = Paths.get(steps.env.WORKSPACE, MROPipelineUtil.REPOS_BASE_DIR, "A").toString()
        def repoDir = util.createDirectory(repoPath)
        def repos = createProject().repositories

        when:
        util.loadPipelineConfig(repoPath, repos[0])

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Error: unable to parse component metadata. Required file '${MROPipelineUtil.COMPONENT_METADATA_FILE_NAME}' does not exist in repository '${repos[0].id}'."

        cleanup:
        repoDir.deleteDir()
    }

    def "load multiple repos' pipeline configs"() {
        given:
        def steps = Spy(util.PipelineSteps)
        def util = new MROPipelineUtil(steps, Mock(GitUtil))

        def repoPathA = Paths.get(steps.env.WORKSPACE, MROPipelineUtil.REPOS_BASE_DIR, "A").toString()
        def repoDirA = util.createDirectory(repoPathA)
        def componentMetadataFileA = Paths.get(repoPathA, MROPipelineUtil.COMPONENT_METADATA_FILE_NAME)
        def pipelineConfigFileA = Paths.get(repoPathA, MROPipelineUtil.PipelineConfig.FILE_NAMES.first())

        def repoPathB = Paths.get(steps.env.WORKSPACE, MROPipelineUtil.REPOS_BASE_DIR, "B").toString()
        def repoDirB = util.createDirectory(repoPathB)
        def componentMetadataFileB = Paths.get(repoPathB, MROPipelineUtil.COMPONENT_METADATA_FILE_NAME)
        def pipelineConfigFileB = Paths.get(repoPathB, MROPipelineUtil.PipelineConfig.FILE_NAMES.first())

        def repoPathC = Paths.get(steps.env.WORKSPACE, MROPipelineUtil.REPOS_BASE_DIR, "C").toString()
        def repoDirC = util.createDirectory(repoPathC)
        def componentMetadataFileC = Paths.get(repoPathC, MROPipelineUtil.COMPONENT_METADATA_FILE_NAME)
        def pipelineConfigFileC = Paths.get(repoPathC, MROPipelineUtil.PipelineConfig.FILE_NAMES.first())

        def repos = createProject().repositories

        when:
        componentMetadataFileA << """
        id: myId-A
        name: myName-A
        description: myDescription-A
        supplier: mySupplier-A
        version: myVersion-A
        references: myReferences-A
        """

        pipelineConfigFileB << """
        dependencies:
          - A
        
        phases:
          build:
            type: Makefile
            target: build
        """

        componentMetadataFileB << """
        id: myId-B
        name: myName-B
        description: myDescription-B
        supplier: mySupplier-B
        version: myVersion-B
        references: myReferences-B
        """

        pipelineConfigFileC << """
        dependencies:
          - B
        
        phases:
          test:
            type: ShellScript
            script: test.sh
        """

        componentMetadataFileC << """
        id: myId-C
        name: myName-C
        description: myDescription-C
        supplier: mySupplier-C
        version: myVersion-C
        references: myReferences-C
        """

        def result = util.loadPipelineConfigs(repos.clone())

        then:
        def expected = [
            repos[0] << [
                metadata: [
                    id: "myId-A",
                    name: "myName-A",
                    description: "myDescription-A",
                    supplier: "mySupplier-A",
                    version: "myVersion-A",
                    references: "myReferences-A"
                ],
                pipelineConfig: []
            ],
            repos[1] << [
                metadata: [
                    id: "myId-B",
                    name: "myName-B",
                    description: "myDescription-B",
                    supplier: "mySupplier-B",
                    version: "myVersion-B",
                    references: "myReferences-B"
                ],
                pipelineConfig: [
                    dependencies: ["A"],
                    phases: [
                        build: [
                            type: "Makefile",
                            target: "build"
                        ]
                    ]
                ]
            ],
            repos[2] << [
                metadata: [
                    id: "myId-C",
                    name: "myName-C",
                    description: "myDescription-C",
                    supplier: "mySupplier-C",
                    version: "myVersion-C",
                    references: "myReferences-C"
                ],
                pipelineConfig: [
                    dependencies: ["B"],
                    phases: [
                        test: [
                            type: "ShellScript",
                            script: "test.sh"
                        ]
                    ]
                ]
            ]
        ]

        result == expected

        cleanup:
        repoDirA.deleteDir()
        repoDirB.deleteDir()
        repoDirC.deleteDir()
    }

    def "load project metadata"() {
        given:
        def steps = Spy(util.PipelineSteps)
        steps.sh(_) >> "https://github.com/my-org/my-pipeline-repo.git"
        def git = Mock(GitUtil)
        def util = new MROPipelineUtil(steps, git)

        def file = Files.createTempFile(Paths.get(steps.env.WORKSPACE), "metadata-", ".yml").toFile()

        when:
        file << """
            id: myId
            name: myName
            description: myDescription
            repositories:
              - id: A
                url: https://github.com/my-org/my-repo-A.git
                branch: master
              - id: B
                name: my-repo-B
                branch: master
              - id: C
        """
        def result = util.loadProjectMetadata(file.name)

        then:
        def expected = [
            id: "myId",
            name: "myName",
            description: "myDescription",
            data: [
                build: [:],
                documents: [:],
                git: [
                    commit: git.getCommit(),
                    url: git.getURL()
                ]
            ],
            repositories: [
                [
                    id: "A",
                    url: "https://github.com/my-org/my-repo-A.git",
                    branch: "master",
                    type: MROPipelineUtil.PipelineConfig.REPO_TYPE_ODS_CODE,
                    data: [
                        documents: [:]
                    ]
                ],
                [
                    id: "B",
                    url: "https://github.com/my-org/my-repo-B.git",
                    branch: "master",
                    type: MROPipelineUtil.PipelineConfig.REPO_TYPE_ODS_CODE,
                    data: [
                        documents: [:]
                    ]
                ],
                [
                    id: "C",
                    url: "https://github.com/my-org/myid-C.git",
                    branch: "master",
                    type: MROPipelineUtil.PipelineConfig.REPO_TYPE_ODS_CODE,
                    data: [
                        documents: [:]
                    ]
                ]
            ],
            capabilities: []
        ]

        result == expected

        cleanup:
        file.delete()
    }

    def "load project metadata with invalid file"() {
        given:
        def steps = Spy(util.PipelineSteps)
        def util = new MROPipelineUtil(steps, Mock(GitUtil))

        when:
        util.loadProjectMetadata(null)

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Error: unable to parse project meta data. 'filename' is undefined."
    }

    def "load project metadata with non-existent file"() {
        given:
        def steps = Spy(util.PipelineSteps)
        def util = new MROPipelineUtil(steps, Mock(GitUtil))

        when:
        def filename = "myFile"
        util.loadProjectMetadata(filename)

        then:
        def e = thrown(RuntimeException)
        e.message == "Error: unable to load project meta data. File '${steps.env.WORKSPACE}/${filename}' does not exist."
    }

    def "load project metadata with invalid id"() {
        given:
        def steps = Spy(util.PipelineSteps)
        def util = new MROPipelineUtil(steps, Mock(GitUtil))

        def file = Files.createTempFile(Paths.get(steps.env.WORKSPACE), "metadata-", ".yml").toFile()

        when:
        file << """
            name: myName
        """
        util.loadProjectMetadata(file.name)

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Error: unable to parse project meta data. Required attribute 'id' is undefined."

        cleanup:
        file.delete()
    }

    def "load project metadata with invalid name"() {
        given:
        def steps = Spy(util.PipelineSteps)
        def util = new MROPipelineUtil(steps, Mock(GitUtil))

        def file = Files.createTempFile(Paths.get(steps.env.WORKSPACE), "metadata-", ".yml").toFile()

        when:
        file << """
            id: myId
        """
        util.loadProjectMetadata(file.name)

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Error: unable to parse project meta data. Required attribute 'name' is undefined."

        cleanup:
        file.delete()
    }
    
    def "load project metadata with invalid description"() {
        given:
        def steps = Spy(util.PipelineSteps)
        def util = new MROPipelineUtil(steps, Mock(GitUtil))

        def file = Files.createTempFile(Paths.get(steps.env.WORKSPACE), "metadata-", ".yml").toFile()

        when:
        file << """
            id: myId
            name: myName
            repositories:
              - id: A
        """
        def result = util.loadProjectMetadata(file.name)

        then:
        result.description == ""

        cleanup:
        file.delete()
    }
    
    def "load project metadata with undefined repositories"() {
        given:
        def steps = Spy(util.PipelineSteps)
        def util = new MROPipelineUtil(steps, Mock(GitUtil))

        def file = Files.createTempFile(Paths.get(steps.env.WORKSPACE), "metadata-", ".yml").toFile()

        when:
        file << """
            id: myId
            name: myName
        """
        def result = util.loadProjectMetadata(file.name)

        then:
        result.repositories == []

        cleanup:
        file.delete()
    }

    def "load project metadata with invalid repository id"() {
        given:
        def steps = Spy(util.PipelineSteps)
        def util = new MROPipelineUtil(steps, Mock(GitUtil))

        def file = Files.createTempFile(Paths.get(steps.env.WORKSPACE), "metadata-", ".yml").toFile()

        when:
        file << """
            id: myId
            name: myName
            repositories:
              - name: A
        """
        util.loadProjectMetadata(file.name)

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Error: unable to parse project meta data. Required attribute 'repositories[0].id' is undefined."

        when:
        file.text = """
            id: myId
            name: myName
            repositories:
              - id: A
              - name: B
        """
        util.loadProjectMetadata(file.name)

        then:
        e = thrown(IllegalArgumentException)
        e.message == "Error: unable to parse project meta data. Required attribute 'repositories[1].id' is undefined."

        cleanup:
        file.delete()
    }

    def "load project metadata with invalid repository url"() {
        given:
        def steps = Spy(util.PipelineSteps)
        def util = new MROPipelineUtil(steps, Mock(GitUtil))

        def file = Files.createTempFile(Paths.get(steps.env.WORKSPACE), "metadata-", ".yml").toFile()

        when:
        file << """
            id: myId
            name: myName
            repositories:
              - name: A
        """
        util.loadProjectMetadata(file.name)

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Error: unable to parse project meta data. Required attribute 'repositories[0].id' is undefined."

        when:
        file.text = """
            id: myId
            name: myName
            repositories:
              - id: A
              - name: B
        """
        util.loadProjectMetadata(file.name)

        then:
        e = thrown(IllegalArgumentException)
        e.message == "Error: unable to parse project meta data. Required attribute 'repositories[1].id' is undefined."

        cleanup:
        file.delete()
    }

    def "walk repo directories"() {
        given:
        def steps = Spy(util.PipelineSteps)
        def util = new MROPipelineUtil(steps, Mock(GitUtil))

        def repoDirA = util.createDirectory(Paths.get(steps.env.WORKSPACE, MROPipelineUtil.REPOS_BASE_DIR, "A").toString())
        def repoDirB = util.createDirectory(Paths.get(steps.env.WORKSPACE, MROPipelineUtil.REPOS_BASE_DIR, "B").toString())
        def repoDirC = util.createDirectory(Paths.get(steps.env.WORKSPACE, MROPipelineUtil.REPOS_BASE_DIR, "C").toString())

        def repos = createProject().repositories
        def visitor = Mock(Closure)

        when:
        util.walkRepoDirectories(repos, visitor)

        then:
        1 * visitor("${steps.env.WORKSPACE}/${MROPipelineUtil.REPOS_BASE_DIR}/A", repos[0])
        1 * visitor("${steps.env.WORKSPACE}/${MROPipelineUtil.REPOS_BASE_DIR}/B", repos[1])
        1 * visitor("${steps.env.WORKSPACE}/${MROPipelineUtil.REPOS_BASE_DIR}/C", repos[2])

        cleanup:
        repoDirA.deleteDir()
        repoDirB.deleteDir()
        repoDirC.deleteDir()
    }
}
