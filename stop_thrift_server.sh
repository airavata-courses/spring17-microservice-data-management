#!/bin/bash

echo "## Stopping CustomerService and OrderService Servers"
ps -ef | grep CustomerService | grep -v grep | awk '{print $2}' | xargs kill
ps -ef | grep OrderService | grep -v grep | awk '{print $2}' | xargs kill

echo "## Complete!"
exit $?
