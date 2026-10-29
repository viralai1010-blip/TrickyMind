android {
    signingConfigs {
        create("release") {
            val keystoreFile = file("trickymind.keystore")
            if (keystoreFile.exists()) {
                storeFile = keystoreFile
                storePassword = System.getenv("KEYSTORE_PASSWORD") ?: "AbdoAbdo1997"
                keyAlias = System.getenv("KEY_ALIAS") ?: "trickymind"
                keyPassword = System.getenv("KEY_PASSWORD") ?: "AbdoAbdo1997"
            }
        }
    }
    buildTypes {
        getByName("release") {
            val keystoreFile = file("trickymind.keystore")
            if (keystoreFile.exists()) {
                signingConfig = signingConfigs.getByName("release")
            }
            isMinifyEnabled = false
        }
    }
}
