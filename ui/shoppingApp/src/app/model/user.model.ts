export interface User {
    id?: number;          // optional for signup/new users
    name: string;
    email: string;
    password?: string;    // optional for fetching users without password
    role: 'User' | 'Admin';
    is_active?: boolean;
    create_at?: string;
    update_at?: string;
}
