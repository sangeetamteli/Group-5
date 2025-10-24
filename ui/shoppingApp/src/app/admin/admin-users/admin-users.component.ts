import { Component, OnInit } from '@angular/core';
import { UserService } from 'src/app/service/user.service';
import { User } from 'src/app/model/user.model';

@Component({
  selector: 'app-admin-users',
  templateUrl: './admin-users.component.html',
  styleUrls: ['./admin-users.component.scss']
})
export class AdminUsersComponent implements OnInit {
  users: User[] = [];
  editingUser: User | null = null;
  showConfirmModal = false;
  userToDelete: number | null = null;

  constructor(private userService: UserService) { }

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers() {
    this.userService.getUsers().subscribe({
      next: (data: User[]) => this.users = data,
      error: (err) => console.error('Failed to load users', err)
    });
  }

  editUser(user: User) {
    this.editingUser = { ...user };
  }

  saveUser() {
    if (!this.editingUser) return;
    this.userService.updateUser(this.editingUser).subscribe({
      next: () => {
        this.loadUsers();
        this.editingUser = null;
      },
      error: (err) => console.error('Update failed', err)
    });
  }
}

