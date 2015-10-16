git branch | grep -e '^\*' | sed -e 's/^\* //g' | sed -e 's/^origin\///g' -e 's/[\/ ]/_/g'
