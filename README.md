# react-native-android-apk-installer

## Getting started

`$ npm install react-native-android-apk-installer --save`

### Mostly automatic installation

`$ react-native link react-native-android-apk-installer`

## Usage

Android APK installer that handles user's interaction on installation dialog.

You can use react-native-fs to download the apk file:

```javascript
import RNFS from 'react-native-fs'
import AndroidApkInstaller from 'react-native-android-apk-installer';

const filePath = RNFS.DocumentDirectoryPath + '/com.domain.example.apk';
const download = RNFS.downloadFile({
    fromUrl: 'apk file download url',
    toFile: filePath,
    progress: res => {
        console.log((res.bytesWritten / res.contentLength).toFixed(2));
    },
    progressDivider: 1
});

download.promise.then(result => {
    if(result.statusCode == 200){
        AndroidApkInstaller.install(filePath).then(data => {
            console.log("Installation success")
        })
        .catch(error => {
            console.log("Fail to install")
        })
    }
});
```
