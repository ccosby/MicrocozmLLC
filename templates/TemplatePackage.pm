#!/usr/bin/env perl -w

package TemplatePackage;

use strict;

use Carp ();
use Exporter;
use Scalar::Util ();

# The basics of an Exporter package
my $VERSION   = '1.0-SNAPSHOT';
my @ISA       = qw(Exporter);
my @EXPORT    = ();
my @EXPORT_OK = ();

__PACKAGE__->main() unless caller;

my $isa = sub { Scalar::Util::blessed $_[0] and $_[0]->isa($_[1]); };

sub smart_sub
{
	# This allows us to call smart_sub as an instance or a self method
	my $self = shift;
	unshift(@_, $self) unless $self->$isa(__PACKAGE__);
	1;
}

sub instance_sub
{
	my $self = shift;
	Carp::croak 'instance_sub is only allowed in instance context' unless $self->$isa(__PACKAGE__);
}

sub new
{
	my $class = shift;
	my (undef, undef) = my @original_args = @_;

	my $self = ();
	$self->{name} = undef;
	return ( bless( $self, $class ) );
}

sub main
{
	my $self = shift;
	printf "This is %s::main()\n", __PACKAGE__;
}

1;

# vim: set ts=4 sw=4 noexpandtab fdm=marker filetype=perl :
