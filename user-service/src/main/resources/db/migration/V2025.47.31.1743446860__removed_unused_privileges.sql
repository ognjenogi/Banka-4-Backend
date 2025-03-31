-- Remove permission bits that weren't being used.  This prevents them being
-- misinterpreted as some future permission.
UPDATE clients SET permission_bits = permission_bits & 0x1;
UPDATE employees SET permission_bits = permission_bits & 0x1;
