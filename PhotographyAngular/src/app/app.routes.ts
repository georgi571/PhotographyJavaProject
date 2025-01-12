import {RouterModule, Routes} from '@angular/router';
import {IntroComponent} from './intro/intro.component';
import {NgModule} from '@angular/core';
import {HomeComponent} from './home/home.component';
import {ChallengesComponent} from './challenges/challenges.component';
import {LeaderboardsComponent} from './leaderboards/leaderboards.component';
import {AboutComponent} from './about/about.component';
import {ContactComponent} from './contact/contact.component';
import {AuthComponent} from './auth/auth.component';
import {LoginComponent} from './auth/login/login.component';
import {RegistrationComponent} from './auth/registration/registration.component';
import {ProfileComponent} from './user/profile/profile.component';
import {AdminComponent} from './admin/admin.component';
import {ApproveUsersComponent} from './approve-users/approve-users.component';
import {ChangeRolesComponent} from './change-roles/change-roles.component';
import {BanUsersComponent} from './ban-users/ban-users.component';
import {FeedbackMessagesComponent} from './feedback-messages/feedback-messages.component';
import {AdminPermissionsComponent} from './admin/admin-permissions/admin-permissions.component';
import {ModeratorPermissionsComponent} from './moderator-permissions/moderator-permissions.component';
import {ProfileEditComponent} from './user/profile-edit/profile-edit.component';
import {EditInformationComponent} from './user/profile-edit/edit-information/edit-information.component';
import {EditEmailComponent} from './user/profile-edit/edit-email/edit-email.component';
import {EditUsernameComponent} from './user/profile-edit/edit-username/edit-username.component';
import {EditPasswordComponent} from './user/profile-edit/edit-password/edit-password.component';
import {EditPictureComponent} from './user/profile-edit/edit-picture/edit-picture.component';

export const routes: Routes = [
    {path: '', component: IntroComponent},
    {path: 'home', component: HomeComponent},
    {path: 'challenges', component: ChallengesComponent},
    {path: 'leaderboards', component: LeaderboardsComponent},
    {path: 'about', component: AboutComponent},
    {path: 'contacts', component: ContactComponent},
    {path: 'auth', component: AuthComponent},
    {path: 'users/login', component: LoginComponent},
    {path: 'users/register', component: RegistrationComponent},
    {path: 'profile', component: ProfileComponent},
    {path: 'profile/edit',
        component: ProfileEditComponent,
        children: [
            {path: 'information', component: EditInformationComponent},
            {path: 'password', component: EditPasswordComponent},
            {path: 'username', component: EditUsernameComponent},
            {path: 'email', component: EditEmailComponent},
            {path: 'picture', component: EditPictureComponent},
        ]
    },
    {
        path: 'admin',
        component: AdminComponent,
        children: [
            {path: 'approve-users', component: ApproveUsersComponent},
            {path: 'change-roles', component: ChangeRolesComponent},
            {path: 'ban-users', component: BanUsersComponent},
            {path: 'feedback', component: FeedbackMessagesComponent},
            {path: 'admin-permission', component: AdminPermissionsComponent},
            {path: 'moderator-permission', component: ModeratorPermissionsComponent},
        ]
    },
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
