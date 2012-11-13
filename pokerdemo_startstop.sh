#!/bin/bash
# start-stop script for pokerdemo
#
# Based on template from http://werxltd.com/wp/2012/01/05/simple-init-d-script-template/
# Tias, 2012-09-07

HOME="/home/pokerdemo"
DAEMON_PATH="$HOME/PokerbotServer/opentestbed"

DAEMON=ant
DAEMONOPTS="PrologBotServer.1"

NAME=pokerdemo
DESC="Poker demo app, backend"
PIDFILE="$HOME/$NAME.pid"
#SCRIPTNAME=/etc/init.d/$NAME
DEBUG=false

case "$1" in
start)
	printf "%-50s" "Starting $NAME..."
	cd $DAEMON_PATH
	if $DEBUG; then
		PID=`nohup $DAEMON $DAEMONOPTS < /dev/null >/tmp/$NAME.out 2>/tmp/$NAME.err & echo $!`
	else
		PID=`nohup $DAEMON $DAEMONOPTS < /dev/null > /dev/null 2>&1 & echo $!`
	fi
	#echo "Saving PID" $PID " to " $PIDFILE
        if [ -z $PID ]; then
            printf "%s\n" "Fail"
        else
            echo $PID > $PIDFILE
            printf "%s\n" "Ok"
        fi
;;
status)
        printf "%-50s" "Checking $NAME..."
        if [ -f $PIDFILE ]; then
            PID=`cat $PIDFILE`
            if [ -z "`ps axf | grep ${PID} | grep -v grep`" ]; then
                printf "%s\n" "Process dead but pidfile exists"
            else
                echo "Running"
            fi
        else
            printf "%s\n" "Service not running"
        fi
;;
stop)
        printf "%-50s" "Stopping $NAME"
            PID=`cat $PIDFILE`
            cd $DAEMON_PATH
        if [ -f $PIDFILE ]; then
            kill $PID
            printf "%s\n" "Ok"
            rm -f $PIDFILE
        else
            printf "%s\n" "pidfile not found"
        fi
;;

restart)
  	$0 stop
  	$0 start
;;

*)
        echo "Usage: $0 {status|start|stop|restart}"
        exit 1
esac
