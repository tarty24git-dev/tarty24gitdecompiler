#!/bin/bash
echo "Stopping CTH Trade Finance System (Linux)..."
PID=$(lsof -t -i :8080)
if [ -n "$PID" ]; then
    kill -9 $PID
    echo "CTH Trade Finance System (PID: $PID) stopped."
else
    echo "No process running on port 8080."
fi
