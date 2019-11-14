db.createUser(
        {
            user: "owner",
            pwd: "renwo",
            roles: [
                {
                    role: "dbOwner",
                    db: "appdb"
                }
            ]
        }
);

