
import { Injectable, Injector } from "@angular/core";
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpResponse } from "@angular/common/http";
import { Observable } from "rxjs";
import { map } from 'rxjs/operators';
import { IdentityService } from "./identity.service";
//adds the jwt token to all requests
@Injectable()
export class TokenInterceptor implements HttpInterceptor {

    constructor(private identityService:IdentityService){
    }
    intercept(req: HttpRequest<any>,
              next: HttpHandler): Observable<HttpEvent<any>> {



        let cloned:HttpRequest<any>;
        let idToken = this.identityService.getToken();
        if (idToken!=null&&this.identityService.isUserLogged()) {
            cloned = req.clone({headers: req.headers.append("Authorization","Bearer " + idToken)});
        }
        else
        {
            cloned=req;
        }
        return next.handle(cloned);
        /*
        return next.handle(cloned).pipe(
            map((event: HttpEvent<any>) => {
                if (event instanceof HttpResponse) {
                    console.log('event--->>>', event);
                }
                return event;
            }));
            */
    }


}
