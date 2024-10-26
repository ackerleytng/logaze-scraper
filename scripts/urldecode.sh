#!/bin/bash

# Credit goes to https://stackoverflow.com/a/37840948

function urldecode() { : "${*//+/ }"; echo -e "${_//%/\\x}"; }

urldecode $1
