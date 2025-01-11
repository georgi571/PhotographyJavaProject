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
