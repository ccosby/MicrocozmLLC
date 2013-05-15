#!/usr/bin/env perl

use strict;
use warnings;

use version; our $VERSION = qv('1.0.0');

use TemplatePackage;

my $t_pkg = TemplatePackage->new();
$t_pkg->main();

# vim: set ts=4 sw=4 noexpandtab fdm=marker filetype=perl :
