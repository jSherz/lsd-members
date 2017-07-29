import { Table, Column, Model, HasMany, PrimaryKey } from 'sequelize-typescript';

@Table({
    createdAt: 'created_at',
    updatedAt: 'updated_at',
    tableName: 'members',
    timestamps: true,
})
export class Member extends Model<Member> {

    @Column({ primaryKey: true })
    uuid: string;

    @Column({ field: 'first_name' })
    firstName: string;

    @Column({ field: 'last_name' })
    lastName: string;

    @Column({ field: 'phone_number' })
    phoneNumber: string;

    @Column({ field: 'email' })
    email: string;

    @Column({ field: 'last_jump' })
    lastJump: Date;

    @Column({ field: 'weight' })
    weight: number;

    @Column({ field: 'height' })
    height: number;

    @Column({ field: 'driver' })
    driver: boolean;

    @Column({ field: 'organiser' })
    organiser: boolean;

    @Column({ field: 'social_user_id' })
    socialUserId: string;

}
