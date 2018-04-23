#! bin/bash

git clone https://$GH_TOKEN_FOR_HTTPS@github.com/KiiPlatform/thing-if-AndroidSDK.git
cd thing-if-AndroidSDK
git checkout gh-pages && git config user.email 'satoshi.kumano@kii.com' && git config user.name 'satoshi kumano'
git rm -r --ignore-unmatch api-doc && mkdir -p api-doc
cp -r ../thingif/build/outputs/javadoc api-doc
echo git add api-doc && git commit -m 'updated doc' && git push origin gh-pages
