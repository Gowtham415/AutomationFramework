#Arg 1: /Automation_artifacts 
#Arg 2:CPM
BUILD_ARTIFACTS_PATH=$1
TEXTURA_PRODUCT=$2
AUTOMATION_SERVER= ${COMPUTERNAME}
ARTIFACTS_EXCLUSIONS=/Automation/Textura/Framework/src/main/scripts/jenkins
JENKINS_JOB_NAME=${JOB_NAME}
JENKINS_BUILD_ID=${BUILD_ID}
JENKINS_BUILD_URL=${BUILD_URL}
JENKINS_BUILD_TAG=${BUILD_TAG}
JENKINS_BUILD_NUMBER=${BUILD_NUMBER}
JENKINS_WORKSPACE=${WORKSPACE}
JENKINS_HOME=${JENKINS_HOME}
JENKINS_BUILD=${JENKINS_JOB_NAME}_${JENKINS_BUILD_NUMBER}
JENKINS_BUILD_LOG=${BUILD_ARTIFACTS_PATH}/${JENKINS_BUILD}/${JENKINS_BUILD}_INFO.log
echo 'Saving build artifacts in: ' ${BUILD_ARTIFACTS_PATH}/${JENKINS_BUILD}
mkdir ${BUILD_ARTIFACTS_PATH}/${JENKINS_BUILD}
echo 'Copying build artifacts...'
#its probably easier to understand by specifying which artifacts should be copied rather than which ones shouldn't be copied
#"%WINDOWS_SYSTEM32%\xcopy.exe" /S /R /Y /Q /EXCLUDE:%ARTIFACTS_EXCLUSIONS%\archive_build_artifacts_excl.txt "%JENKINS_WORKSPACE%\%TEXTURA_PRODUCT%\target" "%BUILD_ARTIFACTS_PATH%\%JENKINS_BUILD%\"
cp -r ${JENKINS_WORKSPACE}/${TEXTURA_PRODUCT}/target/maven-status ${BUILD_ARTIFACTS_PATH}/${JENKINS_BUILD}/
cp -r ${JENKINS_WORKSPACE}/${TEXTURA_PRODUCT}/target/surefire-reports ${BUILD_ARTIFACTS_PATH}/${JENKINS_BUILD}/
cp -r ${JENKINS_WORKSPACE}/${TEXTURA_PRODUCT}/target/test-downloads ${BUILD_ARTIFACTS_PATH}/${JENKINS_BUILD}/
cp -r ${JENKINS_WORKSPACE}/${TEXTURA_PRODUCT}/target/test-screenshots ${BUILD_ARTIFACTS_PATH}/${JENKINS_BUILD}/
cp ${JENKINS_WORKSPACE}/${TEXTURA_PRODUCT}/testconfig.xml ${BUILD_ARTIFACTS_PATH}/${JENKINS_BUILD}/
cp ${JENKINS_WORKSPACE}/${TEXTURA_PRODUCT}/tng* ${BUILD_ARTIFACTS_PATH}/${JENKINS_BUILD}/
cp ${JENKINS_WORKSPACE}/${TEXTURA_PRODUCT}/environment.xml ${BUILD_ARTIFACTS_PATH}/${JENKINS_BUILD}/
echo "Saving build artifacts info in: ${JENKINS_BUILD_LOG}"
echo "*                                              ">> ${JENKINS_BUILD_LOG}
echo "* Automation build on ${COMPUTERNAME}          ">> ${JENKINS_BUILD_LOG}
echo "*                                              ">> ${JENKINS_BUILD_LOG}
echo "Jenkins Build URL   : ${JENKINS_BUILD_URL}     ">> ${JENKINS_BUILD_LOG}
echo "Jenkins Build Start : ${JENKINS_BUILD_ID}      ">> ${JENKINS_BUILD_LOG}
echo "Jenkins Build Num   : ${JENKINS_BUILD_NUMBER}  ">> ${JENKINS_BUILD_LOG}
echo "Jenkins Job Name    : ${JENKINS_JOB_NAME}      ">> ${JENKINS_BUILD_LOG}
echo "Jenkins Build Tag   : ${JENKINS_BUILD_TAG}     ">> ${JENKINS_BUILD_LOG}
echo "Jenkins Home        : ${JENKINS_HOME}          ">> ${JENKINS_BUILD_LOG}
echo "Product             : ${TEXTURA_PRODUCT}       ">> ${JENKINS_BUILD_LOG}
echo "Build Server        : ${AUTOMATION_SERVER}     ">> ${JENKINS_BUILD_LOG}
echo "Build Workspace     : ${JENKINS_WORKSPACE}     ">> ${JENKINS_BUILD_LOG}
echo "Build Artifacts     : ${BUILD_ARTIFACTS_PATH}  ">> ${JENKINS_BUILD_LOG}