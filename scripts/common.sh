#!/bin/bash -u

_bold=""
_underline=""
_standout=""
_normal=""
_black=""
_red=""
_green=""
_yellow=""
_blue=""
_magenta=""
_cyan=""
_white=""

# check if stdout is a terminal...
if [ -t 1 ]; then

    # see if it supports colors...
    _ncolors=$(tput colors)

    if test -n "$_ncolors" && test $_ncolors -ge 8; then
        _bold="$(tput bold)"
        _underline="$(tput smul)"
        _standout="$(tput smso)"
        _normal="$(tput sgr0)"
        _black="$(tput setaf 0)"
        _red="$(tput setaf 1)"
        _green="$(tput setaf 2)"
        _yellow="$(tput setaf 3)"
        _blue="$(tput setaf 4)"
        _magenta="$(tput setaf 5)"
        _cyan="$(tput setaf 6)"
        _white="$(tput setaf 7)"
    fi
fi

# Display INFO message
echo_info() {
    echo -e "${_green}$*${_normal}"
}

# Display WARN message
echo_warn() {
    echo -e "${_yellow}$*${_normal}"
}

# Display ERROR message
echo_error() {
    echo -e "${_bold}${_red}[ERROR] $*${_normal}"
}

# Compare two version strings
# From: https://www.baeldung.com/linux/compare-dot-separated-version-string
vercomp() {
    if [[ $1 == $2 ]]
    then
        return 0
    fi
    local IFS=.
    local i ver1=($1) ver2=($2)
    # fill empty fields in ver1 with zeros
    for ((i=${#ver1[@]}; i<${#ver2[@]}; i++))
    do
        ver1[i]=0
    done
    for ((i=0; i<${#ver1[@]}; i++))
    do
        if [[ -z ${ver2[i]} ]]
        then
            # fill empty fields in ver2 with zeros
            ver2[i]=0
        fi
        if ((10#${ver1[i]} > 10#${ver2[i]}))
        then
            return 1
        fi
        if ((10#${ver1[i]} < 10#${ver2[i]}))
        then
            return 2
        fi
    done
    return 0
}
