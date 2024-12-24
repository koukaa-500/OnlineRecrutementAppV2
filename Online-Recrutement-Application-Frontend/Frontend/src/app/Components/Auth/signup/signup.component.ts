import { Component } from '@angular/core';
import { AuthenticationService } from 'src/app/services/authentication.service';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent {
constructor(private authService : AuthenticationService){}
loginUser = {
  email: '',
  password: '',
};

signupUser = {
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
};
errorMessage = '';
successMessage = '';
onSignUp() {
  if (!this.signupUser.username || !this.signupUser.email || !this.signupUser.password) {
    this.errorMessage = 'All fields are required!';
    return;
  }
  if(this.signupUser.password !== this.signupUser.confirmPassword){
    this.errorMessage = 'Password and Confirm password must be the same';
    return;
  }

  this.authService.signup(this.signupUser).subscribe(
    (response) => {
      
      this.successMessage = 'Signup successful!';
      this.errorMessage = '';
      
      
      this.signupUser.email = '';
      this.signupUser.username = '';
      this.signupUser.password = '';
      this.signupUser.confirmPassword = '';
    },
    (error) => {
        this.successMessage = '';
        this.errorMessage = error.value;
     
      
    }
  );
}

onLogin(){
  if (!this.loginUser.email || !this.loginUser.password) {
    this.errorMessage = 'All fields are required!';
    return;
  }
  this.authService.login(this.loginUser).subscribe(
    (response:any) => {
      
      
      const token = response.token;
      console.log('Token:', token);
      
      
      
      
      this.loginUser.email = '';
      
      this.loginUser.password = '';
      
    },
    (error) => {
        
     console.log(error);
     
      
    }
  )
}

}
