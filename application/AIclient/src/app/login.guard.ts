import {Injectable, OnInit} from '@angular/core'
import { IdentityService } from './identity.service';
import {CanActivate} from "@angular/router";
import {Router} from '@angular/router'

//redirect user if already logged
@Injectable()
export class LoginGuard implements CanActivate
{
    constructor(private identityService: IdentityService, private router: Router) {}

    canActivate(): boolean
    {
        if (this.identityService.isUserLogged())
        {
            let username=this.identityService.getCurrentUsername();
            let role=this.identityService.getCurrentUserRole();
            this.router.navigate([`/login/${role}/${username}`]);
            return false;
        }
        else
        {
            return true;
        }
    }

}
