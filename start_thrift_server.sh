#!/bin/bash

echo "## Killing any existing thrift server processes"
ps -ef | grep CustomerService | grep -v grep | awk '{print $2}' | xargs kill
ps -ef | grep OrderService | grep -v grep | awk '{print $2}' | xargs kill

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

echo "## Building CustomerService Project"
cd CustomerService && mvn clean install > /dev/null
if [ "$?" -ne 0 ]; then
	echo "CustomerService project could not be built. Please verify the error and fix. Exiting" && cd ..
	exit
fi

echo "## Building OrderService Project"
cd ../OrderService && mvn clean install > /dev/null
if [ "$?" -ne 0 ]; then
        echo "OrderService project could not be built. Please verify the error and fix. Exiting" && cd ..
        exit
fi

echo "## Starting the CustomerService Thrift Server"
cd ../CustomerService/target
java -jar CustomerService-1.0.jar >> customerservice.log 2>&1 &
echo "CustomerService thrift server running!"

echo "## Starting the OrderService Thrift Server"
cd ../../OrderService/target
java -jar OrderService-1.0.jar >> orderservice.log 2>&1 &
echo "OrderService thrift server running!"

echo "## Complete!"
exit $?
