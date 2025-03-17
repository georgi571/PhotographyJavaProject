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
import {ApproveUsersComponent} from './admin/approve-users/approve-users.component';
import {ChangeRolesComponent} from './admin/change-roles/change-roles.component';
import {BanUsersComponent} from './admin/ban-users/ban-users.component';
import {FeedbackMessagesComponent} from './admin/feedback-messages/feedback-messages.component';
import {AdminPermissionsComponent} from './admin/admin-permissions/admin-permissions.component';
import {ModeratorPermissionsComponent} from './admin/moderator-permissions/moderator-permissions.component';
import {ProfileEditComponent} from './user/profile-edit/profile-edit.component';
import {EditInformationComponent} from './user/profile-edit/edit-information/edit-information.component';
import {EditEmailComponent} from './user/profile-edit/edit-email/edit-email.component';
import {EditUsernameComponent} from './user/profile-edit/edit-username/edit-username.component';
import {EditPasswordComponent} from './user/profile-edit/edit-password/edit-password.component';
import {EditPictureComponent} from './user/profile-edit/edit-picture/edit-picture.component';
import {ChallengeListComponent} from './challenges/challenge-list/challenge-list.component';
import {ChallengeDetailsComponent} from './challenges/challenge-details/challenge-details.component';
import {FriendsFollowersComponent} from './user/friends-followers/friends-followers.component';
import {AllFriendsComponent} from './user/friends-followers/all-friends/all-friends.component';
import {AllFollowersComponent} from './user/friends-followers/all-followers/all-followers.component';
import {AllFollowingComponent} from './user/friends-followers/all-following/all-following.component';
import {
    ReceiveFriendRequestComponent
} from './user/friends-followers/receive-friend-request/receive-friend-request.component';
import {SendFriendRequestComponent} from './user/friends-followers/send-friend-request/send-friend-request.component';
import {BlockUsersComponent} from './user/friends-followers/block-users/block-users.component';
import {PointsLeaderboardComponent} from './leaderboards/points-leaderboard/points-leaderboard.component';
import {ChallengesLeaderboardComponent} from './leaderboards/challenges-leaderboard/challenges-leaderboard.component';
import {MonthLeaderboardComponent} from './leaderboards/month-leaderboard/month-leaderboard.component';
import {RisingLeaderboardComponent} from './leaderboards/rising-leaderboard/rising-leaderboard.component';
import {AuthRequiredComponent} from './auth-required/auth-required.component';
import {ServerDownComponent} from './server-down/server-down.component';
import {ReportComponent} from './report/report.component';
import {ReportUsersComponent} from './report/report-users/report-users.component';
import {ReportPicturesComponent} from './report/report-pictures/report-pictures.component';
import {ReportCommentsComponent} from './report/report-comments/report-comments.component';

export const routes: Routes = [
    {path: '', component: IntroComponent},
    {path: 'home', component: HomeComponent},
    {path: 'challenges', component: ChallengesComponent},
    {path: 'challenges/list', component: ChallengeListComponent},
    {path: 'challenge/:id', component: ChallengeDetailsComponent},
    {path: 'leaderboards', component: LeaderboardsComponent},
    { path: 'leaderboards/points', component: PointsLeaderboardComponent },
    { path: 'leaderboards/challenges', component: ChallengesLeaderboardComponent },
    { path: 'leaderboards/months', component: MonthLeaderboardComponent },
    { path: 'leaderboards/rising', component: RisingLeaderboardComponent },
    {path: 'about', component: AboutComponent},
    {path: 'contacts', component: ContactComponent},
    {path: 'auth', component: AuthComponent},
    {path: 'auth-required', component: AuthRequiredComponent},
    {path: 'users/login', component: LoginComponent},
    {path: 'users/register', component: RegistrationComponent},
    {
        path: 'profile/edit', component: ProfileEditComponent,
        children: [
            {path: 'information', component: EditInformationComponent},
            {path: 'password', component: EditPasswordComponent},
            {path: 'username', component: EditUsernameComponent},
            {path: 'email', component: EditEmailComponent},
            {path: 'picture', component: EditPictureComponent},
        ]
    },
    {
        path: 'profile', component: FriendsFollowersComponent,
        children: [
            {path: 'friends', component: AllFriendsComponent},
            {path: 'followers', component: AllFollowersComponent},
            {path: 'following', component: AllFollowingComponent},
            {path: 'sent-requests', component: SendFriendRequestComponent},
            {path: 'received-requests', component: ReceiveFriendRequestComponent},
            {path: 'blocked-users', component: BlockUsersComponent}
        ]
    },
    {path: 'profile/username/:username', component: ProfileComponent},
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
    {path: 'server-down', component: ServerDownComponent},
    { path: 'reports', component: ReportComponent, children: [
            { path: 'users', component: ReportUsersComponent },
            { path: 'picture', component: ReportPicturesComponent },
            { path: 'comments', component: ReportCommentsComponent }
        ] }
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
