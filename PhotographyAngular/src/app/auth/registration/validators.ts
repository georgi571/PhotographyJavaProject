import {FormGroup} from '@angular/forms';
import {AbstractControl, ValidationErrors} from '@angular/forms';

export function confirmPasswordValidator(control: AbstractControl): ValidationErrors | null {
    const formGroup = control as FormGroup;
    const password = formGroup.get('password')?.value;
    const confirmPassword = formGroup.get('confirmPassword')?.value;

    if (password && confirmPassword && password !== confirmPassword) {
        return {passwordMismatch: true};
    }
    return null;
}

export function dateNotInFutureValidator(control: AbstractControl): ValidationErrors | null {
    if (!control.value) {
        return null;
    }

    const selectedDate = new Date(control.value);
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    return selectedDate > today ? { futureDate: true } : null;
}
