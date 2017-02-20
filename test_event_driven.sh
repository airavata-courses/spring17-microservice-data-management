#!/bin/bash

echo "## Killing any existing client process"
ps -ef | grep CommonClient | grep -v grep | awk '{print $2}' | xargs kill

echo "## Checking if Maven is installed"
mvn --version > /dev/null 2>&1
if [ "$?" -ne 0 ]; then
	echo "Maven is not installed on this machine, or has not been added to Path. Exiting"
	exit
fi

echo "## Checking if Java is installed"
java -version > /dev/null 2>&1
if [ "$?" -ne 0 ]; then
        echo "Java is not installed on this machine, or has not been added to Path. Exiting"
        exit
fi

# Check if CommonClient JAR exists
if [ ! -f CommonClient/target/CommonClient-1.0.jar ]; then
	echo "## Building the CommonClient project"
	cd CommonClient && mvn clean install > /dev/null
	cd ..
fi


SERVICE=$1
echo "## Running requested client service (output below)."
printf '%*s\n' "${COLUMNS:-$(tput cols)}" '' | tr ' ' -
java -jar CommonClient/target/CommonClient-1.0.jar $SERVICE
printf '%*s\n' "${COLUMNS:-$(tput cols)}" '' | tr ' ' -

echo "## Complete!"
exit $?
