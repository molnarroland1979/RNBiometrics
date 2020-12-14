import { NativeModules } from "react-native";

export const { Biometrics } = NativeModules;

export const openBiometric = (onSuccess, onFail) => {
  Biometrics.openBiometricDialog(onSuccess, onFail);
};

export const getBiometricType = callback => {
  Biometrics.getCurrentBiometricType(callback);
};

export const BiometricsTypes = {
  FINGERPRINT_FEATURE : 'feature_fingerprint',
  FACE_FEATURE : 'feature_face',
  NONE : 'feature_none'
}
