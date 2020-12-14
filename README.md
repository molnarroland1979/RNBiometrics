# react-native-biometrics

THIS IS THE WORK OF LUCIAN BERESESCU AND PROPERTY OF SV.

## Getting started

`$ yarn add git+https://github.com/molnarroland1979/RNBiometrics`

### Mostly automatic installation (react < 0.59)

`$ react-native link react-native-biometrics`

## Usage
```javascript
import {openBiometric, getBiometricType, BiometricsTypes} from 'react-native-biometrics'

// The type will be one of BiometricsTypes 
  // FINGERPRINT_FEATURE
  // FACE_FEATURE
  // NONE'
getBiometricType((error, type) => {
  console.log(`Error: ${error}, Current type: ${type}`)
})

openBiometric(
      (error, response) => {
        if (response.result === true) {
          //Success biometric authentication
        }
      },
      (error, response) => {
        switch (response.error_code) {
          case 10: 
            // User touch outside (Android bug)
            break
          case 7: 
            // authentication failed (just for Android, iOS handles it internal)
            break
          case 13: 
            //authentication canceled by user
            break
          default:
            break
        }
      }
    )
```
