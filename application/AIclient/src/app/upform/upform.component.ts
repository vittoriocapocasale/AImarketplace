import { Input, Output, Component, EventEmitter,  OnInit } from '@angular/core';
import { FormGroup, FormControl } from '@angular/forms';
import { Observable } from 'rxjs';
//form for user and password
@Component({
  selector: 'app-upform',
  templateUrl: './upform.component.html',
  styleUrls: ['./upform.component.css']
})
export class UpformComponent implements OnInit {

  constructor() { }
  form: FormGroup = new FormGroup({
    username: new FormControl(''),
    password: new FormControl(''),
  });

  onSubmit(){
    if (this.form.valid) {
      this.credentials$.emit(this.form.value);
    }
  }


  @Input() invalidCredentials: boolean | null; //variable controlling if credentials are not valid
  @Input() goodCredentials: boolean | null; //variable controlling if credentials are valid
  @Input() formTitle: string | null; //title to show in the form
  @Input() usernamePH: string; //label for the username field
  @Input() passwordPH: string; //label for the password field

  @Output() credentials$: EventEmitter<FormGroup> = new EventEmitter(); //when submit button is pressed, the credentials are emitted

  ngOnInit() {
  }

}
