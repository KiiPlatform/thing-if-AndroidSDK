echo $(grep -o "version.*" ../sdk-info.txt | grep -o '[0-9]\+\.[0-9]\+\.[0-9]\+')
