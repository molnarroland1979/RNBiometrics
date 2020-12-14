#import "Biometrics.h"
#import <LocalAuthentication/LocalAuthentication.h>

@implementation Biometrics

RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(getCurrentBiometricType: (RCTResponseSenderBlock)callback)
{
    LAContext *context = [[LAContext alloc] init];
    NSError *la_error = nil;
    BOOL canEvaluatePolicy = [context canEvaluatePolicy:LAPolicyDeviceOwnerAuthenticationWithBiometrics error:&la_error];

    if (canEvaluatePolicy) {
      NSString *biometryType = [self getBiometryType:context];
      callback(@[[NSNull null], biometryType]);
    } else {
      callback(@[[NSNull null], @"feature_none"]);
    }
}

- (NSString *)getBiometryType:(LAContext *)context
{
  if (@available(iOS 11, *)) {
    return (context.biometryType == LABiometryTypeFaceID) ? @"feature_face" : @"feature_fingerprint";
  }

  return @"TouchID";
}

RCT_EXPORT_METHOD(openBiometricDialog:(RCTResponseSenderBlock)resolve rejecter:(RCTResponseSenderBlock)reject) {
  dispatch_async(dispatch_get_global_queue( DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
      NSString *promptMessage = @"Log in";

    LAContext *context = [[LAContext alloc] init];
    context.localizedFallbackTitle = @"";

    [context evaluatePolicy:LAPolicyDeviceOwnerAuthenticationWithBiometrics localizedReason:promptMessage reply:^(BOOL success, NSError *biometricError) {
      if (success) {
        NSDictionary *result = @{
          @"result": @(YES)
        };
        resolve(@[[NSNull null], result]);
      } else if (biometricError.code == LAErrorUserCancel ||
                 biometricError.code == LAErrorTouchIDLockout ||
                 biometricError.code == LAErrorBiometryLockout)
      {
        NSDictionary *result = @{
          @"result": @(NO),
          @"error_code": @(13)
        };
        reject(@[[NSNull null], result]);
      } else  {
        NSDictionary *result = @{
          @"result": @(NO),
          @"error_code": @(13)
        };
        reject(@[[NSNull null], result]);
      }
    }];
  });
}


@end
