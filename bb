#!/bin/bash
## Inspired by: https://gist.github.com/schacon/e9e743dee2e92db9a464619b99e94eff

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$SCRIPT_DIR/git-utils"

check_git_repo

echo -e "${GREEN}Fetching branch information...${NO_COLOR}"

main_branch=$(get_main_branch)
current_branch=$(get_current_branch)

print_branch_info() {
    local ahead="$1"
    local behind="$2"
    local branch="$3"
    local time="$4"
    local is_current="$5"

    if [[ "$is_current" == "true" ]]; then
        printf "${BOLD}${UNDERLINE}${GREEN}%-5s ${RED}%-6s ${BOLD_GREEN}%-30s ${YELLOW}%-20s${NO_COLOR}\n" "$ahead" "$behind" "$branch" "$time"
    else
        printf "${GREEN}%-5s ${RED}%-6s ${BLUE}%-30s ${YELLOW}%-20s${NO_COLOR}\n" "$ahead" "$behind" "$branch" "$time"
    fi
}

format_string="%(objectname:short)@%(refname:short)@%(committerdate:relative)"
IFS=$'\n'

printf "${GREEN}%-5s ${RED}%-6s ${BLUE}%-30s ${YELLOW}%-20s${NO_COLOR}\n" "Ahead" "Behind" "Branch" "Last Commit"
printf "${GREEN}%-5s ${RED}%-6s ${BLUE}%-30s ${YELLOW}%-20s${NO_COLOR}\n" "-----" "------" "------------------------------" "-------------------"

for branchdata in $(git for-each-ref --sort=-authordate --format="$format_string" refs/heads/); do
    sha=$(echo "$branchdata" | cut -d '@' -f1)
    branch=$(echo "$branchdata" | cut -d '@' -f2)
    time=$(echo "$branchdata" | cut -d '@' -f3)

    if [[ "$branch" != "$main_branch" ]]; then
        ahead_behind=$(git rev-list --left-right --count "$main_branch"..."$branch" 2>/dev/null)
        ahead=$(echo "$ahead_behind" | awk '{print $2}')
        behind=$(echo "$ahead_behind" | awk '{print $1}')
        is_current="false"
        [[ "$branch" == "$current_branch" ]] && is_current="true"

        print_branch_info "$ahead" "$behind" "$branch" "$time" "$is_current"
    fi
done

