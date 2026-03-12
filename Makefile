JDK_DIR := $(HOME)/.jdk-25
JDK_TAR := jdk25.tar.gz
JDK_URL := https://api.adoptium.net/v3/binary/latest/25/ga/linux/x64/jdk/hotspot/normal/eclipse
JAVAFX_PROJECT_DIR := Jraffic
LOCAL_MAVEN_DIR = $(HOME)/maven
LOCAL_MAVEN_VERSION = 3.9.13
MAVEN_URL = https://archive.apache.org/dist/maven/maven-3/3.9.13/binaries/apache-maven-3.9.13-bin.tar.gz

export JAVA_HOME := $(JDK_DIR)
export PATH := $(JAVA_HOME)/bin:$(PATH)

all: mvn jdk run
	@echo "Java 25 ready"

run:
	@echo "Running..."
	@mvn -f $(JAVAFX_PROJECT_DIR)/pom.xml clean javafx:run

clean:
	@echo "Cleaning..."
	@mvn -f $(JAVAFX_PROJECT_DIR)/pom.xml clean > /dev/null
	@rm -rf $(JAVAFX_PROJECT_DIR)/target
	@echo "Cleaned."

mvn:
	@if command -v mvn >/dev/null 2>&1; then \
		echo "Maven Already installed, Skipping..."; \
	else \
    cd $(HOME); \
    echo "Maven not found. Installing locally..."; \
    echo "Downloading Maven $(LOCAL_MAVEN_VERSION)..."; \
    wget -q "$(MAVEN_URL)"; \
    tar -xzf apache-maven-$(LOCAL_MAVEN_VERSION)-bin.tar.gz; \
    mv "apache-maven-$(LOCAL_MAVEN_VERSION)" "$(LOCAL_MAVEN_DIR)"; \
    echo 'export MAVEN_HOME="$(LOCAL_MAVEN_DIR)"' >> ~/.zshrc; \
    echo 'export PATH="$$MAVEN_HOME/bin:$$PATH"' >> ~/.zshrc; \
    zsh; \
    echo "Maven installed locally at $(LOCAL_MAVEN_DIR)"; \
	fi;

jdk:
	@if [ -d "$(JDK_DIR)" ]; then \
		echo "Java 25 already installed."; \
	else \
		echo "Installing Java 25 locally..."; \
		mkdir -p $(JDK_DIR); \
		wget -O $(JDK_TAR) $(JDK_URL); \
		tar -xzf $(JDK_TAR) -C $(JDK_DIR) --strip-components=1 ; \
		rm $(JDK_TAR); \
		echo "Java 25 installed in $(JDK_DIR)"; \
	fi 

check:
	@echo JAVA_HOME=$(JAVA_HOME)
	@$(JAVA_HOME)/bin/java -version