#!/bin/bash
echo "Starting CTH Trade Finance System (Linux) with 8GB Maximum Memory Settings..."
nohup java -Xms4g -Xmx8g -jar target/cth-trade-finance-1.0.0-SNAPSHOT.jar > cth_app.log 2>&1 &
echo "CTH Trade Finance System started in background. Log file: cth_app.log"
