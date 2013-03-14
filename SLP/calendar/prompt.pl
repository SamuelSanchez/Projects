#!/usr/bin/perl

#Command line arguments should be as the following format:
# wave_file:Text_to_be_recorded
#ex: data1.wav:Hello world, I am ready to record this!

#Store all arguments
#Use join to group them together into a string
$arg= join( " ", @ARGV );

#Split the string into wave file name and data to be saved
#@store=split( /:/, $arg );

#Save wave and data to talk
#$wave_name=$store[0];
#$text_to_record=$store[1];

#print "Wave Name: $wave_name\n";
#print "Record   : $text_to_record\n";

#$BASEDIR="/home/eduardo/Research/SpokenLanguages/Dev-SLP-v.1/tts";
#Get the 'tts' absolute path
use Cwd;
my $BASEDIR =  getcwd; 
$BASEDIR.="/tts/";
print $BASEDIR;
#Now use festival
my $filename = "/tmp/temp.scm";
open OUTPUT, ">$filename" or die "Can't open '$filename'for writing.\n";
print OUTPUT '(load"'.$BASEDIR.'/festvox/SLP_Calendar_xyz_ldom.scm")' . "\n";
print OUTPUT '(voice_SLP_Calendar_xyz_ldom)' . "\n";
print OUTPUT '(Parameter.set \'Audio_Method\'Audio_Command)' . "\n";
print OUTPUT '(Parameter.set \'Audio_Required_Rate 16000)'. "\n";
print OUTPUT '(Parameter.set \'Audio_Required_Format\'wav)' . "\n";
print OUTPUT '(Parameter.set \'Audio_Command "cp $FILE'." testing.wav".'")' . "\n";
print OUTPUT '(SayText "'. $arg.'")' . "\n";
close OUTPUT;

system "cd $BASEDIR; festival --batch $filename";
system "cd $BASEDIR; play testing.wav";
