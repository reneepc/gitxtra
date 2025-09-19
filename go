#!/usr/bin/env bash

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$SCRIPT_DIR/git-utils"

check_git_repo

has_fzf() { command -v fzf >/dev/null 2>&1; }
is_tty()  { [[ -t 1 ]]; }
has_switch() { git switch -h >/dev/null 2>&1; }

list_branches_with_dates() {
  git for-each-ref --sort=-committerdate \
    --format="%(refname:short)|%(committerdate:relative)" refs/heads/
}

list_branches() {
  git for-each-ref --sort=-committerdate --format="%(refname:short)" refs/heads/
}

switch_branch() {
  local b="$1"
  if has_switch; then git switch "$b"; else git checkout "$b"; fi
}

fzf_pick_branch() {
  local pattern="${1-}" list sel branch
  if [[ -n "$pattern" ]]; then
    list="$(list_branches_with_dates | grep -i -- "$pattern" || true)"
  else
    list="$(list_branches_with_dates)"
  fi
  [[ -n "$list" ]] || { echo "No branch matches."; return 1; }

  sel="$(
    printf "%s\n" "$list" \
    | fzf --ansi --height=95% --reverse \
          --delimiter='|' --with-nth=1,2 \
          --header=$'↑↓ navigate • Enter to switch' \
          --preview 'git log --decorate --oneline --graph --date=relative -n 40 -- "$(echo {} | awk -F"|" "{print \$1}")"' \
          --preview-window=right,60%
  )" || return 1

  branch="$(printf "%s" "$sel" | awk -F"|" '{print $1}')"
  switch_branch "$branch"
}

numbered_pick_branch() {
  local pattern="${1-}" list count idx branch
  if [[ -n "$pattern" ]]; then
    list="$(list_branches | grep -i -- "$pattern" || true)"
  else
    list="$(list_branches)"
  fi
  list="$(printf "%s\n" "$list" | sed '/^$/d')"
  count="$(printf "%s\n" "$list" | wc -l | tr -d ' ')"

  (( count > 0 )) || { echo "No branch matches."; return 1; }
  if (( count == 1 )); then
    branch="$(printf "%s\n" "$list")"
    switch_branch "$branch"; return $?
  fi

  nl -ba <<<"$list"
  read -r -p "Pick a branch number [1-$count]: " idx
  case "$idx" in ''|*[!0-9]*) echo "Invalid selection."; return 1;; esac
  (( idx >= 1 && idx <= count )) || { echo "Out of range."; return 1; }
  branch="$(printf "%s\n" "$list" | sed -n "${idx}p")"
  switch_branch "$branch"
}

main() {
  local pattern="${1-}"
  if has_fzf && is_tty; then
    fzf_pick_branch "$pattern"
  else
    numbered_pick_branch "$pattern"
  fi
}

main "$@"
