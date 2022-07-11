#!/bin/sh
java -Xms1024M -Xmx4G -cp ./target/tqks-elastic-provider-0.5.0-SNAPSHOT-jar-with-dependencies.jar devtests.TestHarness
