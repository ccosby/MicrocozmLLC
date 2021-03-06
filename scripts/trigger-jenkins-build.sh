#!/bin/bash

# Use this in your repos/hooks/post-commit
# REPOS="$1"
# "$REPOS"/hook-scripts/post-commit/trigger-jenkins-build.sh "$REPOS" $REV

unset http_proxy https_proxy ftp_proxy

REPOS="$1"
REV="$2"

# No environment is passed to svn hook scripts; set paths to external tools explicitly:
WGET=/usr/bin/wget
SVNLOOK=/usr/bin/svnlook

# If your server requires authentication, it is recommended that you set up a .netrc file to store your username and password
# Better yet, since Jenkins v. 1.426, use the generated API Token in place of the password
# See https://wiki.jenkins-ci.org/display/JENKINS/Authenticating+scripted+clients
# Since no environment is passed to hook scripts, you need to set $HOME (where your .netrc lives)
# By convention, this should be the home dir of whichever user is running the svn process (i.e. apache)
HOME=/var/www/

USERNAME=<your username>
API_TOKEN=<your api token>
HTTP_AUTH="--http-user=$USERNAME --http-password=$API_TOKEN"

UUID=`$SVNLOOK uuid $REPOS`
NOTIFY_URL="subversion/${UUID}/notifyCommit?rev=${REV}"
CRUMB_ISSUER_URL='crumbIssuer/api/xml?xpath=concat(//crumbRequestField,":",//crumb)'

notifyCI() {
	# URL to Hudson/Jenkins server application (with protocol, hostname, port and deployment descriptor if needed)
	CISERVER=$1

	# Check if "[X] Prevent Cross Site Request Forgery exploits" is activated
	# so we can present a valid crumb or a proper header
	HEADER="Content-Type:text/plain;charset=UTF-8"
	CRUMB=`$WGET -q $HTTP_AUTH --auth-no-challenge --output-document - ${CISERVER}/${CRUMB_ISSUER_URL}`
	if [ "$CRUMB" != "" ]; then HEADER=$CRUMB; fi

	$WGET \
		-q \
		$HTTP_AUTH \
		--auth-no-challenge \
		--header $HEADER \
		--post-data "`$SVNLOOK changed --revision $REV $REPOS`" \
		--output-document "-"\
		--timeout=2 \
		${CISERVER}/${NOTIFY_URL}
}

# The code above was placed in a function so you can easily notify multiple Jenkins/Hudson servers:
notifyCI "http://ci.yoursite.com/ci"

exit 0
