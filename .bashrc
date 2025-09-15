#
# ~/.bashrc
#

# If not running interactively, don't do anything
[[ $- != *i* ]] && return


alias grep='grep --color=auto'

# some useful aliases
alias h='fc -l'
alias j=jobs
alias m="$PAGER"
alias ll='ls -laFo --color'
alias l='ls -l'
alias g='egrep -i'

# # be paranoid
# alias cp='cp -ip'
# alias mv='mv -i'
# alias rm='rm -i'

alias history='fc -l'

# set prompt: ``username@hostname:directory $ ''
# PS1="\u:\w \\$ "
parse_git_branch() {
  git branch 2>/dev/null | sed -n '/\* /s///p'
}
# \[$(tput md)\]
# PS1="\[\e[32m\]\u@\h \[\e[34m\]\w \[\e[33m\]\$(parse_git_branch)\[\e[0m\]\$ "

PS1='\[\e[33m\]\w\[\e[0m\] \[\e[32m\]$(parse_git_branch)\[\e[0m\] \[\e[31m\]$(echo $?)\[\e[0m\] \$ '
# search path for cd(1)
# CDPATH=:$HOME

alias nox="emacsclient -a 'emacs --load /home/sbzorro/git-repo/emacs-d/init.el --daemon' -nw"

export COLORTERM=truecolor
