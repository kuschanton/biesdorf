resource "aws_iam_policy" "allow_kinesis_put_record" {
  policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": [
        "kinesis:PutRecord"
      ],
      "Effect": "Allow",
      "Resource": "arn:aws:kinesis:eu-central-1:123456789000:stream/TaskRouterEvents"
    }
  ]
}
EOF
}

resource "aws_iam_policy_attachment" "role-attach" {
  name       = "role-attachment"
  users      = []
  roles      = [aws_iam_role.event_post.name]
  groups     = []
  policy_arn = aws_iam_policy.allow_kinesis_put_record.arn
}