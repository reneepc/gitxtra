#!/bin/bash

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$SCRIPT_DIR/git-utils"

DEFAULT_LIMIT=5

usage() {
  cat <<'EOF'
Usage: branches-lifetime [--all] [-n NUM] [-h|--help]

  --all       Show all local branches
  -n NUM      Show only the NUM most recent local branches
  -h, --help  Show this help

Defaults to showing the 5 most recent local branches.
EOF
}

require_int() {
  [[ "$1" =~ ^[0-9]+$ ]] || { echo "error: limit must be a non-negative integer"; exit 2; }
}

parse_args() {
  SHOW_ALL=false
  USER_LIMIT=""
  while [[ $# -gt 0 ]]; do
    case "$1" in
      --all) SHOW_ALL=true; shift ;;
      -n) USER_LIMIT="${2-}"; [[ -n "$USER_LIMIT" ]] || { echo "error: -n requires a number"; usage; exit 2; }; shift 2 ;;
      -n[0-9]*) USER_LIMIT="${1#-n}"; shift ;;
      -h|--help) usage; exit 0 ;;
      *) echo "Unknown option: $1"; usage; exit 2 ;;
    esac
  done
  if $SHOW_ALL; then
    EFFECTIVE_LIMIT=0
  else
    EFFECTIVE_LIMIT="${USER_LIMIT:-$DEFAULT_LIMIT}"
    require_int "$EFFECTIVE_LIMIT"
  fi
}

print_header() {
  printf "${GREEN}%-5s ${RED}%-6s ${BLUE}%-30s ${YELLOW}%-20s${NO_COLOR}\n" "Ahead" "Behind" "Branch" "Last Commit"
  printf "${GREEN}%-5s ${RED}%-6s ${BLUE}%-30s ${YELLOW}%-20s${NO_COLOR}\n" "-----" "------" "------------------------------" "-------------------"
}

print_row() {
  local ahead="$1" behind="$2" branch="$3" time="$4" is_current="$5"
  if [[ "$is_current" == "true" ]]; then
    printf "${BOLD}${UNDERLINE}${GREEN}%-5s ${RED}%-6s ${BOLD_GREEN}%-30s ${YELLOW}%-20s${NO_COLOR}\n" "$ahead" "$behind" "$branch" "$time"
  else
    printf "${GREEN}%-5s ${RED}%-6s ${BLUE}%-30s ${YELLOW}%-20s${NO_COLOR}\n" "$ahead" "$behind" "$branch" "$time"
  fi
}

supports_nul_delim() {
  git for-each-ref -z --format="%(refname)" refs/heads/ >/dev/null 2>&1
}

list_branches_nul() {
  local main_branch="$1" current_branch="$2" limit="$3" count=0
  while IFS= read -r -d '' branch && IFS= read -r -d '' time; do
    if [[ "$limit" -gt 0 && "$count" -ge "$limit" ]]; then break; fi
    ahead_behind=$(git rev-list --left-right --count "$main_branch"..."$branch" 2>/dev/null)
    ahead=$(awk '{print $2}' <<<"$ahead_behind")
    behind=$(awk '{print $1}' <<<"$ahead_behind")
    is_current="false"; [[ "$branch" == "$current_branch" ]] && is_current="true"
    print_row "$ahead" "$behind" "$branch" "$time" "$is_current"
    ((count++))
  done < <(git for-each-ref -z --sort=-committerdate \
           --format="%(refname:short)%00%(committerdate:relative)%00" refs/heads/)
}

list_branches_fallback() {
  local main_branch="$1" current_branch="$2" limit="$3" count=0
  while IFS= read -r line; do
    if [[ "$limit" -gt 0 && "$count" -ge "$limit" ]]; then break; fi
    branch="${line%%@*}"
    time="${line#*@}"
    ahead_behind=$(git rev-list --left-right --count "$main_branch"..."$branch" 2>/dev/null)
    ahead=$(awk '{print $2}' <<<"$ahead_behind")
    behind=$(awk '{print $1}' <<<"$ahead_behind")
    is_current="false"; [[ "$branch" == "$current_branch" ]] && is_current="true"
    print_row "$ahead" "$behind" "$branch" "$time" "$is_current"
    ((count++))
  done < <(git for-each-ref --sort=-committerdate \
           --format="%(refname:short)@%(committerdate:relative)" refs/heads/)
}

main() {
  check_git_repo
  parse_args "$@"

  echo -e "${GREEN}Fetching branch information...${NO_COLOR}"
  local main_branch current_branch
  main_branch=$(get_main_branch)
  current_branch=$(get_current_branch)

  print_header
  if supports_nul_delim; then
    list_branches_nul "$main_branch" "$current_branch" "$EFFECTIVE_LIMIT"
  else
    list_branches_fallback "$main_branch" "$current_branch" "$EFFECTIVE_LIMIT"
  fi
}

main "$@"
