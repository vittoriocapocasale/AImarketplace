import {Injectable, OnInit} from '@angular/core'
import { IdentityService } from './identity.service';
import {CanActivate, RouterStateSnapshot, ActivatedRoute, RouterState} from "@angular/router";
import {Router,ActivatedRouteSnapshot} from '@angular/router'
@Injectable()

//check access to the private user area, by checking the user is logged
export class UserGuard implements CanActivate
{
    constructor(private identityService: IdentityService, private router: Router) {}
    canActivate(
        route: ActivatedRouteSnapshot,
        state: RouterStateSnapshot): boolean
    {
        let userId=this.identityService.getCurrentUsername();
        let role=this.identityService.getCurrentUserRole();
        if (this.identityService.isUserLogged())
        {
           if(state.url==`/login/${role}/${userId}`)
            {
                return true;
            }
            else
            {
                this.router.navigate([`/login/${role}/${userId}`]);
                return false;
            }
        }
        else
        {
            this.router.navigate(['/login']);
            return false;
        }
    }

}
