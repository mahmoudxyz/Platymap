package xyz.mahmoudahmed.format

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.nio.charset.StandardCharsets

class YamlFormatDetectorTest {

    private val detector = YamlFormatDetector()

    // Confidence thresholds for validation
    private val HIGH_CONFIDENCE = 0.75f
    private val MEDIUM_CONFIDENCE = 0.5f
    private val LOW_CONFIDENCE = 0.25f
    private val ZERO_CONFIDENCE = 0.0f

    @Test
    fun testEmptyInput() {
        assertEquals(ZERO_CONFIDENCE, detector.detect(byteArrayOf()))
        assertEquals(ZERO_CONFIDENCE, detector.detect("".toByteArray(StandardCharsets.UTF_8)))
    }

    @Test
    fun testSimpleKeyValueYaml() {
        val yaml = """
            name: Test Config
            version: 1.0.0
            description: A simple YAML file
        """.trimIndent()

        val confidence = detector.detect(yaml.toByteArray(StandardCharsets.UTF_8))
        assertTrue(confidence > MEDIUM_CONFIDENCE, "Simple key-value YAML should have medium-high confidence")
    }

    @Test
    fun testComplexYamlWithHierarchy() {
        val yaml = """
            apiVersion: v1
            kind: Service
            metadata:
              name: my-service
              labels:
                app: my-app
            spec:
              selector:
                app: my-app
              ports:
                - protocol: TCP
                  port: 80
                  targetPort: 9376
        """.trimIndent()

        val confidence = detector.detect(yaml.toByteArray(StandardCharsets.UTF_8))
        assertTrue(confidence > HIGH_CONFIDENCE, "Complex hierarchical YAML should have high confidence")
    }

    @Test
    fun testYamlWithDocumentMarkers() {
        val yaml = """
            ---
            # Document start marker
            name: Document
            content: Test
            ...
            ---
            # Another document
            type: Example
            data: Value
            ...
        """.trimIndent()

        val confidence = detector.detect(yaml.toByteArray(StandardCharsets.UTF_8))
        print(confidence)
        assertTrue(confidence > HIGH_CONFIDENCE, "YAML with document markers should have high confidence")
    }

    @Test
    fun testYamlWithAnchorsAndAliases() {
        val yaml = """
            defaults: &defaults
              adapter: postgres
              host: localhost
            
            development:
              database: myapp_development
              <<: *defaults
            
            test:
              database: myapp_test
              <<: *defaults
        """.trimIndent()

        val confidence = detector.detect(yaml.toByteArray(StandardCharsets.UTF_8))
        assertTrue(confidence > HIGH_CONFIDENCE, "YAML with anchors and aliases should have high confidence")
    }


    @Test
    fun testYamlListOnly() {
        val yaml = """
            - item1
            - item2
            - complex_item:
                name: SubItem
                value: 100
            - item3
        """.trimIndent()

        val confidence = detector.detect(yaml.toByteArray(StandardCharsets.UTF_8))
        assertTrue(confidence > MEDIUM_CONFIDENCE, "YAML with only list items should have medium-high confidence")
    }

    @Test
    fun testYamlWithComments() {
        val yaml = """
            # Configuration file
            version: 1.0.0  # Current version
            
            # User settings section
            user:
              # Username for login
              name: admin
              # Access level
              role: administrator
        """.trimIndent()

        val confidence = detector.detect(yaml.toByteArray(StandardCharsets.UTF_8))
        assertTrue(confidence > MEDIUM_CONFIDENCE, "YAML with comments should have medium-high confidence")
    }

    @Test
    fun testYamlWithTags() {
        val yaml = """
            date: !date 2023-01-01
            person: !person
              name: John
              age: 30
            binary: !binary |
              R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7
        """.trimIndent()

        val confidence = detector.detect(yaml.toByteArray(StandardCharsets.UTF_8))
        assertTrue(confidence > MEDIUM_CONFIDENCE, "YAML with tags should have medium-high confidence")
    }

    @Test
    fun testVeryShortYaml() {
        val yaml = "key: value"

        val confidence = detector.detect(yaml.toByteArray(StandardCharsets.UTF_8))
        assertTrue(confidence > LOW_CONFIDENCE && confidence < HIGH_CONFIDENCE,
            "Very short YAML should have moderate confidence")
    }

    @Test
    fun testJsonShouldHaveLowConfidence() {
        val json = """
            {
              "name": "Test",
              "values": [1, 2, 3],
              "nested": {
                "key": "value"
              }
            }
        """.trimIndent()

        val confidence = detector.detect(json.toByteArray(StandardCharsets.UTF_8))
        assertTrue(confidence < LOW_CONFIDENCE, "JSON should have low YAML confidence")
    }

    @Test
    fun testXmlShouldHaveZeroConfidence() {
        val xml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <root>
              <name>Test</name>
              <values>
                <value>1</value>
                <value>2</value>
              </values>
            </root>
        """.trimIndent()

        val confidence = detector.detect(xml.toByteArray(StandardCharsets.UTF_8))
        assertEquals(ZERO_CONFIDENCE, confidence, "XML should have zero YAML confidence")
    }

    @Test
    fun testHtmlShouldHaveZeroConfidence() {
        val html = """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Test Page</title>
            </head>
            <body>
                <h1>Hello World</h1>
                <p>This is a test.</p>
            </body>
            </html>
        """.trimIndent()

        val confidence = detector.detect(html.toByteArray(StandardCharsets.UTF_8))
        assertEquals(ZERO_CONFIDENCE, confidence, "HTML should have zero YAML confidence")
    }

    @Test
    fun testPlainTextShouldHaveLowConfidence() {
        val text = """
            This is a plain text file.
            It has multiple lines.
            But no YAML structure.
            Just regular sentences.
        """.trimIndent()

        val confidence = detector.detect(text.toByteArray(StandardCharsets.UTF_8))
        assertTrue(confidence < LOW_CONFIDENCE, "Plain text should have low YAML confidence")
    }

    @Test
    fun testCodeShouldHaveLowConfidence() {
        val code = """
            function test() {
              const x = 1;
              if (x > 0) {
                return true;
              }
              return false;
            }
        """.trimIndent()

        val confidence = detector.detect(code.toByteArray(StandardCharsets.UTF_8))
        assertTrue(confidence < LOW_CONFIDENCE, "Code should have low YAML confidence")
    }

    @Test
    fun testMixedIndentationYaml() {
        val yaml = """
            root:
              level1:
                - item1
                - item2
            	level2: # This line uses a tab instead of spaces
            	  value: test
        """.trimIndent()

        val confidence = detector.detect(yaml.toByteArray(StandardCharsets.UTF_8))
        assertTrue(confidence > LOW_CONFIDENCE && confidence < HIGH_CONFIDENCE,
            "YAML with mixed indentation should have moderate confidence")
    }

    @Test
    fun testEdgeCaseYamlLookingLikeOtherFormat() {
        val yaml = """
            html: <html>This looks like HTML but isn't</html>
            javascript: function() { return true; }
            xml: <?xml version="1.0"?>
            values:
              - name: test
                value: 100
        """.trimIndent()

        val confidence = detector.detect(yaml.toByteArray(StandardCharsets.UTF_8))
        assertTrue(confidence > LOW_CONFIDENCE,
            "YAML with content that looks like other formats should still have moderate confidence")
    }

    @Test
    fun testComplexDeeplyNestedYaml() {
        val yaml = """
            apiVersion: apps/v1
            kind: Deployment
            metadata:
              name: nginx-deployment
              labels:
                app: nginx
            spec:
              replicas: 3
              selector:
                matchLabels:
                  app: nginx
              template:
                metadata:
                  labels:
                    app: nginx
                spec:
                  containers:
                  - name: nginx
                    image: nginx:1.14.2
                    ports:
                    - containerPort: 80
                    resources:
                      limits:
                        cpu: "1"
                        memory: "512Mi"
                      requests:
                        cpu: "0.5"
                        memory: "256Mi"
                    volumeMounts:
                    - name: config-volume
                      mountPath: /etc/nginx/conf.d
                  volumes:
                  - name: config-volume
                    configMap:
                      name: nginx-config
        """.trimIndent()

        val confidence = detector.detect(yaml.toByteArray(StandardCharsets.UTF_8))
        assertTrue(confidence > HIGH_CONFIDENCE, "Complex deeply nested YAML should have high confidence")
    }

    @Test
    fun testYamlWithManyListsAndEmptyLines() {
        val yaml = """
            environments:
              - name: development
                urls:
                  - http://dev.example.com
                  - http://dev-api.example.com
                
                users:
                  - name: admin
                    role: administrator
                  
                  - name: guest
                    role: viewer
                
              - name: production
                urls:
                  - http://example.com
                  - http://api.example.com
                
                users:
                  - name: system
                    role: administrator
        """.trimIndent()

        val confidence = detector.detect(yaml.toByteArray(StandardCharsets.UTF_8))
        assertTrue(confidence > HIGH_CONFIDENCE, "YAML with many lists and empty lines should have high confidence")
    }

    @Test
    fun testRealWorldCommonYamlFiles() {
        // Docker Compose file example
        val dockerCompose = """
            version: '3'
            services:
              web:
                image: nginx:alpine
                ports:
                  - "80:80"
                volumes:
                  - ./html:/usr/share/nginx/html
              db:
                image: postgres:13
                environment:
                  POSTGRES_PASSWORD: example
                volumes:
                  - postgres_data:/var/lib/postgresql/data
            volumes:
              postgres_data:
        """.trimIndent()

        // GitHub Actions workflow example
        val githubActions = """
            name: CI
            on:
              push:
                branches: [ main ]
              pull_request:
                branches: [ main ]
            jobs:
              build:
                runs-on: ubuntu-latest
                steps:
                - uses: actions/checkout@v2
                - name: Set up JDK
                  uses: actions/setup-java@v2
                  with:
                    java-version: '11'
                    distribution: 'adopt'
                - name: Build with Maven
                  run: mvn -B package --file pom.xml
        """.trimIndent()

        // Kubernetes configuration example
        val kubernetes = """
            apiVersion: v1
            kind: Service
            metadata:
              name: my-service
            spec:
              selector:
                app: MyApp
              ports:
              - protocol: TCP
                port: 80
                targetPort: 9376
        """.trimIndent()

        assertTrue(detector.detect(dockerCompose.toByteArray(StandardCharsets.UTF_8)) > HIGH_CONFIDENCE,
            "Docker Compose YAML should have high confidence")
        assertTrue(detector.detect(githubActions.toByteArray(StandardCharsets.UTF_8)) > HIGH_CONFIDENCE,
            "GitHub Actions YAML should have high confidence")
        assertTrue(detector.detect(kubernetes.toByteArray(StandardCharsets.UTF_8)) > HIGH_CONFIDENCE,
            "Kubernetes YAML should have high confidence")
    }

    @Test
    fun testMalformedYaml() {
        val malformedYaml = """
            valid:
              key: value
            invalid:
            key without proper indentation
            - list item without proper structure
              nested: but invalid
        """.trimIndent()

        val confidence = detector.detect(malformedYaml.toByteArray(StandardCharsets.UTF_8))
        assertTrue(confidence > LOW_CONFIDENCE && confidence < HIGH_CONFIDENCE,
            "Malformed YAML should have moderate confidence")
    }
}
