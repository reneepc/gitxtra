#!/bin/bash
## Inspired by https://gist.github.com/schacon/e9e743dee2e92db9a464619b99e94eff

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$SCRIPT_DIR/git-utils"

check_git_repo

echo -e "${GREEN}Fetching branch information...${NO_COLOR}"

format_string="%(objectname:short)@%(refname:short)@%(committerdate:relative)"
IFS=$'\n'

printf "${GREEN}%-5s ${RED}%-6s ${BLUE}%-30s ${YELLOW}%-20s ${NO_COLOR}%-40s\n" "Ahead" "Behind" "Branch" "Last Commit" " "

printf "${GREEN}%-5s ${RED}%-6s ${BLUE}%-30s ${YELLOW}%-20s ${NO_COLOR}%-40s\n" "-----" "------" "------------------------------" "-------------------" " "

for branchdata in $(git for-each-ref --sort=-authordate --format="$format_string" refs/heads/ --no-merged); do
    sha=$(echo "$branchdata" | cut -d '@' -f1)
    branch=$(echo "$branchdata" | cut -d '@' -f2)
    time=$(echo "$branchdata" | cut -d '@' -f3)
    
    ahead_behind=$(git rev-list --left-right --count HEAD..."$branch")
    ahead=$(echo "$ahead_behind" | cut -f2)
    behind=$(echo "$ahead_behind" | cut -f1)

    printf "${GREEN}%-5s ${RED}%-6s ${BLUE}%-30s ${YELLOW}%-20s ${NO_COLOR}%-40s\n" "$ahead" "$behind" "$branch" "$time" ""
done
