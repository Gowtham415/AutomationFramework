AUTOMATION_ARTIFACTS_PATH='/Automation_artifacts'
AUTOMATION_PATH='/Automation'
TEXTURA_PATH='/Automation/Textura'
REPOSITORY_PATH='/Automation/repository'
JFROG_PATH='/Automation_artifacts/jfrog'
JFROG_REPOSITORY="http://dfwin7qaauto53:8081/artifactory/"
GIT_PATH='/usr/bin/git'
BITBUCKET_PATH="https://bitbucket.texturallc.net/scm/at"

if [ -e "$AUTOMATION_PATH" ]; then
	echo "*"
	echo "*"
	echo "* Automation folder already exists:" $AUTOMATION_PATH
	echo "* Unable to continue"
	echo "*"
	echo "*"
	exit 0
fi

$JFROG_PATH rt dl --url $JFROG_REPOSITORY --user readonly --password QAselenium7 --split-count=0 "tools-maven" $AUTOMATION_PATH/Maven/
$JFROG_PATH rt dl --url $JFROG_REPOSITORY --user readonly --password QAselenium7 --split-count=0 "repository-maven" $REPOSITORY_PATH/maven/
$JFROG_PATH rt dl --url $JFROG_REPOSITORY --user readonly --password QAselenium7 --split-count=0 "repository-resources" $REPOSITORY_PATH/resources/
git clone $BITBUCKET_PATH/textura.git $TEXTURA_PATH
sudo rm -r $TEXTURA_PATH/.git
git clone $BITBUCKET_PATH/framework.git $TEXTURA_PATH/Framework
git clone $BITBUCKET_PATH/cpm.git $TEXTURA_PATH/CPM
echo "*"
echo "*"
echo "* Setup complete"
echo "*"
echo "*"
