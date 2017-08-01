import { Column, HasMany, Model, PrimaryKey, Table } from 'sequelize-typescript';

@Table({
    createdAt: 'created_at',
    tableName: 'members',
    timestamps: true,
    updatedAt: 'updated_at',
})
export class Member extends Model<Member> {

    @Column({ primaryKey: true })
    public uuid: string;

    @Column({ field: 'first_name' })
    public firstName: string;

    @Column({ field: 'last_name' })
    public lastName: string;

    @Column({ field: 'phone_number' })
    public phoneNumber: string;

    @Column({ field: 'email' })
    public email: string;

    @Column({ field: 'last_jump' })
    public lastJump: Date;

    @Column({ field: 'weight' })
    public weight: number;

    @Column({ field: 'height' })
    public height: number;

    @Column({ field: 'driver' })
    public driver: boolean;

    @Column({ field: 'organiser' })
    public organiser: boolean;

    @Column({ field: 'social_user_id' })
    public socialUserId: string;

}
