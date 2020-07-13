declare module 'react-native-android-apk-installer' {

    const AndroidApkInstaller: {
        install: () => Promise<string>;
    };

    export = AndroidApkInstaller;
}