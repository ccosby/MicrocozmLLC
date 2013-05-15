#!/usr/bin/env perl

package TemplatePackage;
use base qw(Exporter);

use strict;
use warnings;

use Carp         ();
use Scalar::Util ();

# The basics of an Exporter package
our $VERSION     = '1.0-SNAPSHOT';
our @EXPORT_OK   = ();
our %EXPORT_TAGS = ( all => [qw(&smart_sub &instance_sub)] );

if ( !caller ) {
    __PACKAGE__->main();
}

my $isa = sub { Scalar::Util::blessed $_[0] and $_[0]->isa( $_[1] ); };

sub smart_sub {

    # This allows us to call smart_sub as an instance or a self method
    # Note that this doesn't work properly if called from a caller() method.
    my ( $self, @args ) = @_;
    if ( !$self->$isa(__PACKAGE__) ) {
        unshift @args, $self;
    }
    use Data::Dumper ();
    print Data::Dumper::Dumper( \@args ) or Carp::carp 'print failed';
    return;
}

sub instance_sub {
    my ( $self, $arg_ref ) = @_;
    if ( !$self->$isa(__PACKAGE__) ) {
        Carp::croak 'instance_sub is only allowed in instance context';
    }
    return;
}

sub new {
    my ( $class, $arg_ref ) = my @original_args = @_;

    my $self = ();
    $self->{name} = undef;
    return ( bless $self, $class );
}

sub main {
    my $self = shift;

    $self->smart_sub(qw(a b c d e));

    #smart_sub(qw(a b c d e));

    printf "This is %s::main()\n", __PACKAGE__;
    return;
}

1;

# vim: set ts=4 sw=4 noexpandtab fdm=marker filetype=perl :
