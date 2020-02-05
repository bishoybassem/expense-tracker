package com.myprojects.expense.authenticator.validation;

import org.passay.DigitCharacterRule;
import org.passay.LengthRule;
import org.passay.LowercaseCharacterRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.passay.UppercaseCharacterRule;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    private static final PasswordValidator PASSWORD_VALIDATOR = new PasswordValidator(Arrays.asList(
            new LengthRule(8, 100),
            new LowercaseCharacterRule(1),
            new UppercaseCharacterRule(1),
            new DigitCharacterRule(1)));

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        RuleResult result = PASSWORD_VALIDATOR.validate(new PasswordData(value));
        if (result.isValid()) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        PASSWORD_VALIDATOR.getMessages(result).stream().forEach(violation -> {
            context.buildConstraintViolationWithTemplate(violation)
                    .addConstraintViolation();
        });
        return false;
    }
}
